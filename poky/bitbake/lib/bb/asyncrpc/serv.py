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
from . import chunkify, DEFAULT_MAX_CHUNK


class ClientError(Exception):
    pass


class ServerError(Exception):
    pass


class AsyncServerConnection(object):
    def __init__(self, reader, writer, proto_name, logger):
        self.reader = reader
        self.writer = writer
        self.proto_name = proto_name
        self.max_chunk = DEFAULT_MAX_CHUNK
        self.handlers = {
            'chunk-stream': self.handle_chunk,
            'ping': self.handle_ping,
        }
        self.logger = logger

    async def process_requests(self):
        try:
            self.addr = self.writer.get_extra_info('peername')
            self.logger.debug('Client %r connected' % (self.addr,))

            # Read protocol and version
            client_protocol = await self.reader.readline()
            if client_protocol is None:
                return

            (client_proto_name, client_proto_version) = client_protocol.decode('utf-8').rstrip().split()
            if client_proto_name != self.proto_name:
                self.logger.debug('Rejecting invalid protocol %s' % (self.proto_name))
                return

            self.proto_version = tuple(int(v) for v in client_proto_version.split('.'))
            if not self.validate_proto_version():
                self.logger.debug('Rejecting invalid protocol version %s' % (client_proto_version))
                return

            # Read headers. Currently, no headers are implemented, so look for
            # an empty line to signal the end of the headers
            while True:
                line = await self.reader.readline()
                if line is None:
                    return

                line = line.decode('utf-8').rstrip()
                if not line:
                    break

            # Handle messages
            while True:
                d = await self.read_message()
                if d is None:
                    break
                await self.dispatch_message(d)
                await self.writer.drain()
        except ClientError as e:
            self.logger.error(str(e))
        finally:
            self.writer.close()

    async def dispatch_message(self, msg):
        for k in self.handlers.keys():
            if k in msg:
                self.logger.debug('Handling %s' % k)
                await self.handlers[k](msg[k])
                return

        raise ClientError("Unrecognized command %r" % msg)

    def write_message(self, msg):
        for c in chunkify(json.dumps(msg), self.max_chunk):
            self.writer.write(c.encode('utf-8'))

    async def read_message(self):
        l = await self.reader.readline()
        if not l:
            return None

        try:
            message = l.decode('utf-8')

            if not message.endswith('\n'):
                return None

            return json.loads(message)
        except (json.JSONDecodeError, UnicodeDecodeError) as e:
            self.logger.error('Bad message from client: %r' % message)
            raise e

    async def handle_chunk(self, request):
        lines = []
        try:
            while True:
                l = await self.reader.readline()
                l = l.rstrip(b"\n").decode("utf-8")
                if not l:
                    break
                lines.append(l)

            msg = json.loads(''.join(lines))
        except (json.JSONDecodeError, UnicodeDecodeError) as e:
            self.logger.error('Bad message from client: %r' % lines)
            raise e

        if 'chunk-stream' in msg:
            raise ClientError("Nested chunks are not allowed")

        await self.dispatch_message(msg)

    async def handle_ping(self, request):
        response = {'alive': True}
        self.write_message(response)


class AsyncServer(object):
    def __init__(self, logger):
        self._cleanup_socket = None
        self.logger = logger
        self.start = None
        self.address = None
        self.loop = None

    def start_tcp_server(self, host, port):
        def start_tcp():
            self.server = self.loop.run_until_complete(
                asyncio.start_server(self.handle_client, host, port)
            )

            for s in self.server.sockets:
                self.logger.debug('Listening on %r' % (s.getsockname(),))
                # Newer python does this automatically. Do it manually here for
                # maximum compatibility
                s.setsockopt(socket.SOL_TCP, socket.TCP_NODELAY, 1)
                s.setsockopt(socket.SOL_TCP, socket.TCP_QUICKACK, 1)

            name = self.server.sockets[0].getsockname()
            if self.server.sockets[0].family == socket.AF_INET6:
                self.address = "[%s]:%d" % (name[0], name[1])
            else:
                self.address = "%s:%d" % (name[0], name[1])

        self.start = start_tcp

    def start_unix_server(self, path):
        def cleanup():
            os.unlink(path)

        def start_unix():
            cwd = os.getcwd()
            try:
                # Work around path length limits in AF_UNIX
                os.chdir(os.path.dirname(path))
                self.server = self.loop.run_until_complete(
                    asyncio.start_unix_server(self.handle_client, os.path.basename(path))
                )
            finally:
                os.chdir(cwd)

            self.logger.debug('Listening on %r' % path)

            self._cleanup_socket = cleanup
            self.address = "unix://%s" % os.path.abspath(path)

        self.start = start_unix

    @abc.abstractmethod
    def accept_client(self, reader, writer):
        pass

    async def handle_client(self, reader, writer):
        # writer.transport.set_write_buffer_limits(0)
        try:
            client = self.accept_client(reader, writer)
            await client.process_requests()
        except Exception as e:
            import traceback
            self.logger.error('Error from client: %s' % str(e), exc_info=True)
            traceback.print_exc()
            writer.close()
        self.logger.debug('Client disconnected')

    def run_loop_forever(self):
        try:
            self.loop.run_forever()
        except KeyboardInterrupt:
            pass

    def signal_handler(self):
        self.logger.debug("Got exit signal")
        self.loop.stop()

    def _serve_forever(self):
        try:
            self.loop.add_signal_handler(signal.SIGTERM, self.signal_handler)
            signal.pthread_sigmask(signal.SIG_UNBLOCK, [signal.SIGTERM])

            self.run_loop_forever()
            self.server.close()

            self.loop.run_until_complete(self.server.wait_closed())
            self.logger.debug('Server shutting down')
        finally:
            if self._cleanup_socket is not None:
                self._cleanup_socket()

    def serve_forever(self):
        """
        Serve requests in the current process
        """
        # Create loop and override any loop that may have existed in
        # a parent process.  It is possible that the usecases of
        # serve_forever might be constrained enough to allow using
        # get_event_loop here, but better safe than sorry for now.
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)
        self.start()
        self._serve_forever()

    def serve_as_process(self, *, prefunc=None, args=()):
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
            self.loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self.loop)
            try:
                self.start()
            finally:
                queue.put(self.address)
                queue.close()

            if prefunc is not None:
                prefunc(self, *args)

            self._serve_forever()

            if sys.version_info >= (3, 6):
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
