#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import abc
import asyncio
import json
import os
import signal
import socket
import sys
import multiprocessing
import logging
from .connection import StreamConnection, WebsocketConnection
from .exceptions import ClientError, ServerError, ConnectionClosedError, InvokeError


class ClientLoggerAdapter(logging.LoggerAdapter):
    def process(self, msg, kwargs):
        return f"[Client {self.extra['address']}] {msg}", kwargs


class AsyncServerConnection(object):
    # If a handler returns this object (e.g. `return self.NO_RESPONSE`), no
    # return message will be automatically be sent back to the client
    NO_RESPONSE = object()

    def __init__(self, socket, proto_name, logger):
        self.socket = socket
        self.proto_name = proto_name
        self.handlers = {
            "ping": self.handle_ping,
        }
        self.logger = ClientLoggerAdapter(
            logger,
            {
                "address": socket.address,
            },
        )
        self.client_headers = {}

    async def close(self):
        await self.socket.close()

    async def handle_headers(self, headers):
        return {}

    async def process_requests(self):
        try:
            self.logger.info("Client %r connected" % (self.socket.address,))

            # Read protocol and version
            client_protocol = await self.socket.recv()
            if not client_protocol:
                return

            (client_proto_name, client_proto_version) = client_protocol.split()
            if client_proto_name != self.proto_name:
                self.logger.debug("Rejecting invalid protocol %s" % (self.proto_name))
                return

            self.proto_version = tuple(int(v) for v in client_proto_version.split("."))
            if not self.validate_proto_version():
                self.logger.debug(
                    "Rejecting invalid protocol version %s" % (client_proto_version)
                )
                return

            # Read headers
            self.client_headers = {}
            while True:
                header = await self.socket.recv()
                if not header:
                    # Empty line. End of headers
                    break
                tag, value = header.split(":", 1)
                self.client_headers[tag.lower()] = value.strip()

            if self.client_headers.get("needs-headers", "false") == "true":
                for k, v in (await self.handle_headers(self.client_headers)).items():
                    await self.socket.send("%s: %s" % (k, v))
                await self.socket.send("")

            # Handle messages
            while True:
                d = await self.socket.recv_message()
                if d is None:
                    break
                try:
                    response = await self.dispatch_message(d)
                except InvokeError as e:
                    await self.socket.send_message(
                        {"invoke-error": {"message": str(e)}}
                    )
                    break

                if response is not self.NO_RESPONSE:
                    await self.socket.send_message(response)

        except ConnectionClosedError as e:
            self.logger.info(str(e))
        except (ClientError, ConnectionError) as e:
            self.logger.error(str(e))
        finally:
            await self.close()

    async def dispatch_message(self, msg):
        for k in self.handlers.keys():
            if k in msg:
                self.logger.debug("Handling %s" % k)
                return await self.handlers[k](msg[k])

        raise ClientError("Unrecognized command %r" % msg)

    async def handle_ping(self, request):
        return {"alive": True}


class StreamServer(object):
    def __init__(self, handler, logger):
        self.handler = handler
        self.logger = logger
        self.closed = False

    async def handle_stream_client(self, reader, writer):
        # writer.transport.set_write_buffer_limits(0)
        socket = StreamConnection(reader, writer, -1)
        if self.closed:
            await socket.close()
            return

        await self.handler(socket)

    async def stop(self):
        self.closed = True


class TCPStreamServer(StreamServer):
    def __init__(self, host, port, handler, logger, *, reuseport=False):
        super().__init__(handler, logger)
        self.host = host
        self.port = port
        self.reuseport = reuseport

    def start(self, loop):
        self.server = loop.run_until_complete(
            asyncio.start_server(
                self.handle_stream_client,
                self.host,
                self.port,
                reuse_port=self.reuseport,
            )
        )

        for s in self.server.sockets:
            self.logger.debug("Listening on %r" % (s.getsockname(),))
            # Newer python does this automatically. Do it manually here for
            # maximum compatibility
            s.setsockopt(socket.SOL_TCP, socket.TCP_NODELAY, 1)
            s.setsockopt(socket.SOL_TCP, socket.TCP_QUICKACK, 1)

            # Enable keep alives. This prevents broken client connections
            # from persisting on the server for long periods of time.
            s.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPIDLE, 30)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPINTVL, 15)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPCNT, 4)

        name = self.server.sockets[0].getsockname()
        if self.server.sockets[0].family == socket.AF_INET6:
            self.address = "[%s]:%d" % (name[0], name[1])
        else:
            self.address = "%s:%d" % (name[0], name[1])

        return [self.server.wait_closed()]

    async def stop(self):
        await super().stop()
        self.server.close()

    def cleanup(self):
        pass


class UnixStreamServer(StreamServer):
    def __init__(self, path, handler, logger):
        super().__init__(handler, logger)
        self.path = path

    def start(self, loop):
        cwd = os.getcwd()
        try:
            # Work around path length limits in AF_UNIX
            os.chdir(os.path.dirname(self.path))
            self.server = loop.run_until_complete(
                asyncio.start_unix_server(
                    self.handle_stream_client, os.path.basename(self.path)
                )
            )
        finally:
            os.chdir(cwd)

        self.logger.debug("Listening on %r" % self.path)
        self.address = "unix://%s" % os.path.abspath(self.path)
        return [self.server.wait_closed()]

    async def stop(self):
        await super().stop()
        self.server.close()

    def cleanup(self):
        os.unlink(self.path)


class WebsocketsServer(object):
    def __init__(self, host, port, handler, logger, *, reuseport=False):
        self.host = host
        self.port = port
        self.handler = handler
        self.logger = logger
        self.reuseport = reuseport

    def start(self, loop):
        import websockets.server

        self.server = loop.run_until_complete(
            websockets.server.serve(
                self.client_handler,
                self.host,
                self.port,
                ping_interval=None,
                reuse_port=self.reuseport,
            )
        )

        for s in self.server.sockets:
            self.logger.debug("Listening on %r" % (s.getsockname(),))

            # Enable keep alives. This prevents broken client connections
            # from persisting on the server for long periods of time.
            s.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPIDLE, 30)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPINTVL, 15)
            s.setsockopt(socket.IPPROTO_TCP, socket.TCP_KEEPCNT, 4)

        name = self.server.sockets[0].getsockname()
        if self.server.sockets[0].family == socket.AF_INET6:
            self.address = "ws://[%s]:%d" % (name[0], name[1])
        else:
            self.address = "ws://%s:%d" % (name[0], name[1])

        return [self.server.wait_closed()]

    async def stop(self):
        self.server.close()

    def cleanup(self):
        pass

    async def client_handler(self, websocket):
        socket = WebsocketConnection(websocket, -1)
        await self.handler(socket)


class AsyncServer(object):
    def __init__(self, logger):
        self.logger = logger
        self.loop = None
        self.run_tasks = []

    def start_tcp_server(self, host, port, *, reuseport=False):
        self.server = TCPStreamServer(
            host,
            port,
            self._client_handler,
            self.logger,
            reuseport=reuseport,
        )

    def start_unix_server(self, path):
        self.server = UnixStreamServer(path, self._client_handler, self.logger)

    def start_websocket_server(self, host, port, reuseport=False):
        self.server = WebsocketsServer(
            host,
            port,
            self._client_handler,
            self.logger,
            reuseport=reuseport,
        )

    async def _client_handler(self, socket):
        address = socket.address
        try:
            client = self.accept_client(socket)
            await client.process_requests()
        except Exception as e:
            import traceback

            self.logger.error(
                "Error from client %s: %s" % (address, str(e)), exc_info=True
            )
            traceback.print_exc()
        finally:
            self.logger.debug("Client %s disconnected", address)
            await socket.close()

    @abc.abstractmethod
    def accept_client(self, socket):
        pass

    async def stop(self):
        self.logger.debug("Stopping server")
        await self.server.stop()

    def start(self):
        tasks = self.server.start(self.loop)
        self.address = self.server.address
        return tasks

    def signal_handler(self):
        self.logger.debug("Got exit signal")
        self.loop.create_task(self.stop())

    def _serve_forever(self, tasks):
        try:
            self.loop.add_signal_handler(signal.SIGTERM, self.signal_handler)
            self.loop.add_signal_handler(signal.SIGINT, self.signal_handler)
            self.loop.add_signal_handler(signal.SIGQUIT, self.signal_handler)
            signal.pthread_sigmask(signal.SIG_UNBLOCK, [signal.SIGTERM])

            self.loop.run_until_complete(asyncio.gather(*tasks))

            self.logger.debug("Server shutting down")
        finally:
            self.server.cleanup()

    def serve_forever(self):
        """
        Serve requests in the current process
        """
        self._create_loop()
        tasks = self.start()
        self._serve_forever(tasks)
        self.loop.close()

    def _create_loop(self):
        # Create loop and override any loop that may have existed in
        # a parent process.  It is possible that the usecases of
        # serve_forever might be constrained enough to allow using
        # get_event_loop here, but better safe than sorry for now.
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

    def serve_as_process(self, *, prefunc=None, args=(), log_level=None):
        """
        Serve requests in a child process
        """

        def run(queue):
            # Create loop and override any loop that may have existed
            # in a parent process.  Without doing this and instead
            # using get_event_loop, at the very minimum the hashserv
            # unit tests will hang when running the second test.
            # This happens since get_event_loop in the spawned server
            # process for the second testcase ends up with the loop
            # from the hashserv client created in the unit test process
            # when running the first testcase.  The problem is somewhat
            # more general, though, as any potential use of asyncio in
            # Cooker could create a loop that needs to replaced in this
            # new process.
            self._create_loop()
            try:
                self.address = None
                tasks = self.start()
            finally:
                # Always put the server address to wake up the parent task
                queue.put(self.address)
                queue.close()

            if prefunc is not None:
                prefunc(self, *args)

            if log_level is not None:
                self.logger.setLevel(log_level)

            self._serve_forever(tasks)

            self.loop.run_until_complete(self.loop.shutdown_asyncgens())
            self.loop.close()

        queue = multiprocessing.Queue()

        # Temporarily block SIGTERM. The server process will inherit this
        # block which will ensure it doesn't receive the SIGTERM until the
        # handler is ready for it
        mask = signal.pthread_sigmask(signal.SIG_BLOCK, [signal.SIGTERM])
        try:
            self.process = multiprocessing.Process(target=run, args=(queue,))
            self.process.start()

            self.address = queue.get()
            queue.close()
            queue.join_thread()

            return self.process
        finally:
            signal.pthread_sigmask(signal.SIG_SETMASK, mask)
