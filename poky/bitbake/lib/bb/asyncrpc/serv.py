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
    def __init__(self, logger, loop=None):
        if loop is None:
            self.loop = asyncio.new_event_loop()
            self.close_loop = True
        else:
            self.loop = loop
            self.close_loop = False

        self._cleanup_socket = None
        self.logger = logger

    def start_tcp_server(self, host, port):
        self.server = self.loop.run_until_complete(
            asyncio.start_server(self.handle_client, host, port, loop=self.loop)
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

    def start_unix_server(self, path):
        def cleanup():
            os.unlink(path)

        cwd = os.getcwd()
        try:
            # Work around path length limits in AF_UNIX
            os.chdir(os.path.dirname(path))
            self.server = self.loop.run_until_complete(
                asyncio.start_unix_server(self.handle_client, os.path.basename(path), loop=self.loop)
            )
        finally:
            os.chdir(cwd)

        self.logger.debug('Listening on %r' % path)

        self._cleanup_socket = cleanup
        self.address = "unix://%s" % os.path.abspath(path)

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

    def serve_forever(self):
        asyncio.set_event_loop(self.loop)
        try:
            self.loop.add_signal_handler(signal.SIGTERM, self.signal_handler)
            signal.pthread_sigmask(signal.SIG_UNBLOCK, [signal.SIGTERM])

            self.run_loop_forever()
            self.server.close()

            self.loop.run_until_complete(self.server.wait_closed())
            self.logger.debug('Server shutting down')
        finally:
            if self.close_loop:
                if sys.version_info >= (3, 6):
                    self.loop.run_until_complete(self.loop.shutdown_asyncgens())
                self.loop.close()

            if self._cleanup_socket is not None:
                self._cleanup_socket()

    def serve_as_process(self, *, prefunc=None, args=()):
        def run():
            if prefunc is not None:
                prefunc(self, *args)
            self.serve_forever()

        # Temporarily block SIGTERM. The server process will inherit this
        # block which will ensure it doesn't receive the SIGTERM until the
        # handler is ready for it
        mask = signal.pthread_sigmask(signal.SIG_BLOCK, [signal.SIGTERM])
        try:
            self.process = multiprocessing.Process(target=run)
            self.process.start()

            return self.process
        finally:
            signal.pthread_sigmask(signal.SIG_SETMASK, mask)
