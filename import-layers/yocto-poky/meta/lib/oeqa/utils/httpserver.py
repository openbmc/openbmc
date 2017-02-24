import http.server
import multiprocessing
import os
from socketserver import ThreadingMixIn

class HTTPServer(ThreadingMixIn, http.server.HTTPServer):

    def server_start(self, root_dir):
        import signal
        signal.signal(signal.SIGTERM, signal.SIG_DFL)
        os.chdir(root_dir)
        self.serve_forever()

class HTTPRequestHandler(http.server.SimpleHTTPRequestHandler):

    def log_message(self, format_str, *args):
        pass

class HTTPService(object):

    def __init__(self, root_dir, host=''):
        self.root_dir = root_dir
        self.host = host
        self.port = 0

    def start(self):
        self.server = HTTPServer((self.host, self.port), HTTPRequestHandler)
        if self.port == 0:
            self.port = self.server.server_port
        self.process = multiprocessing.Process(target=self.server.server_start, args=[self.root_dir])
        self.process.start()

    def stop(self):
        self.server.server_close()
        self.process.terminate()
        self.process.join()
