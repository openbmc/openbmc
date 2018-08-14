#
# Small event handler to automatically open URLs and file
# bug reports at a bugzilla of your choiche
# it uses XML-RPC interface, so you must have it enabled
#
# Before using you must define BUGZILLA_USER, BUGZILLA_PASS credentials,
# BUGZILLA_XMLRPC - uri of xmlrpc.cgi,
# BUGZILLA_PRODUCT, BUGZILLA_COMPONENT - a place in BTS for build bugs
# BUGZILLA_VERSION - version against which to report new bugs
#

def bugzilla_find_bug_report(debug_file, server, args, bugname):
    args['summary'] = bugname
    bugs = server.Bug.search(args)
    if len(bugs['bugs']) == 0:
        print >> debug_file, "Bugs not found"
        return (False,None)
    else: # silently pick the first result
        print >> debug_file, "Result of bug search is "
        print >> debug_file, bugs
        status = bugs['bugs'][0]['status']
        id = bugs['bugs'][0]['id']
        return (not status in ["CLOSED", "RESOLVED", "VERIFIED"],id)

def bugzilla_file_bug(debug_file, server, args, name, text, version):
    args['summary'] = name
    args['comment'] = text
    args['version'] = version
    args['op_sys'] = 'Linux'
    args['platform'] = 'Other'
    args['severity'] = 'normal'
    args['priority'] = 'Normal'
    try:
        return server.Bug.create(args)['id']
    except Exception, e:
        print >> debug_file, repr(e)
        return None

def bugzilla_reopen_bug(debug_file, server, args, bug_number):
    args['ids'] = [bug_number]
    args['status'] = "CONFIRMED"
    try:
        server.Bug.update(args)
        return True
    except Exception, e:
        print >> debug_file, repr(e)
        return False

def bugzilla_create_attachment(debug_file, server, args, bug_number, text, file_name, log, logdescription):
    args['ids'] = [bug_number]
    args['file_name'] = file_name
    args['summary'] = logdescription
    args['content_type'] = "text/plain"
    args['data'] = log
    args['comment'] = text
    try:
        server.Bug.add_attachment(args)
        return True
    except Exception, e:
        print >> debug_file, repr(e)
        return False

def bugzilla_add_comment(debug_file, server, args, bug_number, text):
    args['id'] = bug_number
    args['comment'] = text
    try:
        server.Bug.add_comment(args)
        return True
    except Exception, e:
        print >> debug_file, repr(e)
        return False

addhandler bugzilla_eventhandler
bugzilla_eventhandler[eventmask] = "bb.event.MsgNote bb.build.TaskFailed"
python bugzilla_eventhandler() {
    import glob
    import xmlrpclib, httplib

    class ProxiedTransport(xmlrpclib.Transport):
        def __init__(self, proxy, use_datetime = 0):
            xmlrpclib.Transport.__init__(self, use_datetime)
            self.proxy = proxy
            self.user = None
            self.password = None

        def set_user(self, user):
            self.user = user

        def set_password(self, password):
            self.password = password

        def make_connection(self, host):
            self.realhost = host
            return httplib.HTTP(self.proxy)

        def send_request(self, connection, handler, request_body):
            connection.putrequest("POST", 'http://%s%s' % (self.realhost, handler))
            if self.user != None:
                if self.password != None:
                    auth = "%s:%s" % (self.user, self.password)
                else:
                    auth = self.user
                connection.putheader("Proxy-authorization", "Basic " + base64.encodestring(auth))

    event = e
    data = e.data
    name = bb.event.getName(event)
    if name == "MsgNote":
        # avoid recursion
        return

    if name == "TaskFailed":
        xmlrpc  = data.getVar("BUGZILLA_XMLRPC")
        user    = data.getVar("BUGZILLA_USER")
        passw   = data.getVar("BUGZILLA_PASS")
        product = data.getVar("BUGZILLA_PRODUCT")
        compon  = data.getVar("BUGZILLA_COMPONENT")
        version = data.getVar("BUGZILLA_VERSION")

        proxy   = data.getVar('http_proxy')
        if (proxy):
            import urllib2
            s, u, p, hostport = urllib2._parse_proxy(proxy)
            transport = ProxiedTransport(hostport)
        else:
            transport = None

        server = xmlrpclib.ServerProxy(xmlrpc, transport=transport, verbose=0)
        args = {
            'Bugzilla_login': user,
            'Bugzilla_password': passw,
            'product': product,
            'component': compon}

        # evil hack to figure out what is going on
        debug_file = open(os.path.join(data.getVar("TMPDIR"),"..","bugzilla-log"),"a")

        file = None
        bugname = "%(package)s-%(pv)s-autobuild" % { "package" : data.getVar("PN"),
                                                           "pv"      : data.getVar("PV"),
                                                           }
        log_file = glob.glob("%s/log.%s.*" % (event.data.getVar('T'), event.task))
        text     = "The %s step in %s failed at %s for machine %s" % (e.task, data.getVar("PN"), data.getVar('DATETIME'), data.getVar('MACHINE') )
        if len(log_file) != 0:
            print >> debug_file, "Adding log file %s" % log_file[0]
            file = open(log_file[0], 'r')
            log = file.read()
            file.close();
        else:
            print >> debug_file, "No log file found for the glob"
            log = None

        (bug_open, bug_number) = bugzilla_find_bug_report(debug_file, server, args.copy(), bugname)
        print >> debug_file, "Bug is open: %s and bug number: %s" % (bug_open, bug_number)

        # The bug is present and still open, attach an error log
        if not bug_number:
            bug_number = bugzilla_file_bug(debug_file, server, args.copy(), bugname, text, version)
            if not bug_number:
                print >> debug_file, "Couldn't acquire a new bug_numer, filing a bugreport failed"
            else:
                print >> debug_file, "The new bug_number: '%s'" % bug_number
        elif not bug_open:
            if not bugzilla_reopen_bug(debug_file, server, args.copy(), bug_number):
                print >> debug_file, "Failed to reopen the bug #%s" % bug_number
            else:
                print >> debug_file, "Reopened the bug #%s" % bug_number

        if bug_number and log:
            print >> debug_file, "The bug is known as '%s'" % bug_number
            desc = "Build log for machine %s" % (data.getVar('MACHINE'))
            if not bugzilla_create_attachment(debug_file, server, args.copy(), bug_number, text, log_file[0], log, desc):
                 print >> debug_file, "Failed to attach the build log for bug #%s" % bug_number
            else:
                 print >> debug_file, "Created an attachment for '%s' '%s' '%s'" % (product, compon, bug_number)
        else:
            print >> debug_file, "Not trying to create an attachment for bug #%s" % bug_number
            if not bugzilla_add_comment(debug_file, server, args.copy(), bug_number, text, ):
                 print >> debug_file, "Failed to create a comment the build log for bug #%s" % bug_number
            else:
                 print >> debug_file, "Created an attachment for '%s' '%s' '%s'" % (product, compon, bug_number)

        # store bug number for oestats-client
        if bug_number:
            data.setVar('OESTATS_BUG_NUMBER', bug_number)
}

