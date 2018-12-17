import http.server
import multiprocessing
import os
import traceback
import signal
from socketserver import ThreadingMixIn

class HTTPServer(ThreadingMixIn, http.server.HTTPServer):

    def server_start(self, root_dir, logger):
        os.chdir(root_dir)
        self.serve_forever()

class HTTPRequestHandler(http.server.SimpleHTTPRequestHandler):

    def log_message(self, format_str, *args):
        pass

class HTTPService(object):

    def __init__(self, root_dir, host='', logger=None):
        self.root_dir = root_dir
        self.host = host
        self.port = 0
        self.logger = logger

    def start(self):
        if not os.path.exists(self.root_dir):
            self.logger.info("Not starting HTTPService for directory %s which doesn't exist" % (self.root_dir))
            return

        self.server = HTTPServer((self.host, self.port), HTTPRequestHandler)
        if self.port == 0:
            self.port = self.server.server_port
        self.process = multiprocessing.Process(target=self.server.server_start, args=[self.root_dir, self.logger])

        # The signal handler from testimage.bbclass can cause deadlocks here
        # if the HTTPServer is terminated before it can restore the standard 
        #signal behaviour
        orig = signal.getsignal(signal.SIGTERM)
        signal.signal(signal.SIGTERM, signal.SIG_DFL)
        self.process.start()
        signal.signal(signal.SIGTERM, orig)

        if self.logger:
            self.logger.info("Started HTTPService on %s:%s" % (self.host, self.port))


    def stop(self):
        if hasattr(self, "server"):
            self.server.server_close()
        if hasattr(self, "process"):
            self.process.terminate()
            self.process.join()
        if self.logger:
            self.logger.info("Stopped HTTPService on %s:%s" % (self.host, self.port))

