#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import http.server
import logging
import multiprocessing
import os
import signal
from socketserver import ThreadingMixIn

class HTTPServer(ThreadingMixIn, http.server.HTTPServer):

    def server_start(self, root_dir, logger):
        os.chdir(root_dir)
        self.logger = logger
        self.serve_forever()

class HTTPRequestHandler(http.server.SimpleHTTPRequestHandler):

    def log_message(self, format_str, *args):
        self.server.logger.info(format_str, *args)

class HTTPService:

    def __init__(self, root_dir, host='', port=0, logger=None):
        self.root_dir = root_dir
        self.host = host
        self.port = port
        if logger:
            self.logger = logger.getChild("HTTPService")
        else:
            self.logger = logging.getLogger("HTTPService")

    def start(self):
        if not os.path.exists(self.root_dir):
            self.logger.info("Not starting HTTPService for directory %s which doesn't exist" % (self.root_dir))
            return

        self.server = HTTPServer((self.host, self.port), HTTPRequestHandler)
        if self.port == 0:
            self.port = self.server.server_port
        self.process = multiprocessing.Process(target=self.server.server_start, args=[self.root_dir, self.logger])

        def handle_error(self, request, client_address):
            import traceback
            exception = traceback.format_exc()
            self.logger.warn("Exception when handling %s: %s" % (request, exception))
        self.server.handle_error = handle_error

        # The signal handler from testimage.bbclass can cause deadlocks here
        # if the HTTPServer is terminated before it can restore the standard 
        #signal behaviour
        orig = signal.getsignal(signal.SIGTERM)
        signal.signal(signal.SIGTERM, signal.SIG_DFL)
        self.process.start()
        signal.signal(signal.SIGTERM, orig)

        if self.logger:
            self.logger.info("Started HTTPService for %s on %s:%s" % (self.root_dir, self.host, self.port))


    def stop(self):
        if hasattr(self, "server"):
            self.server.server_close()
        if hasattr(self, "process"):
            self.process.terminate()
            self.process.join()
        if self.logger:
            self.logger.info("Stopped HTTPService on %s:%s" % (self.host, self.port))

if __name__ == "__main__":
    import sys, logging

    logger = logging.getLogger(__name__)
    logging.basicConfig(level=logging.DEBUG)
    httpd = HTTPService(sys.argv[1], port=8888, logger=logger)
    httpd.start()
