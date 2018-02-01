def tinder_http_post(server, selector, content_type, body):
    import httplib
    # now post it
    for i in range(0,5):
        try:
            h = httplib.HTTP(server)
            h.putrequest('POST', selector)
            h.putheader('content-type', content_type)
            h.putheader('content-length', str(len(body)))
            h.endheaders()
            h.send(body)
            errcode, errmsg, headers = h.getreply()
            #print(errcode, errmsg, headers)
            return (errcode,errmsg, headers, h.file)
        except:
            print("Error sending the report!")
            # try again
            pass

    # return some garbage
    return (-1, "unknown", "unknown", None)

def tinder_form_data(bound, dict, log):
    output = []
    # for each key in the dictionary
    for name in dict:
        assert dict[name]
        output.append( "--" + bound )
        output.append( 'Content-Disposition: form-data; name="%s"' % name )
        output.append( "" )
        output.append( dict[name] )
    if log:
        output.append( "--" + bound )
        output.append( 'Content-Disposition: form-data; name="log"; filename="log.txt"' )
        output.append( '' )
        output.append( log )
    output.append( '--' + bound + '--' )
    output.append( '' )

    return "\r\n".join(output)

def tinder_time_string():
    """
    Return the time as GMT
    """
    return ""

def tinder_format_http_post(d,status,log):
    """
    Format the Tinderbox HTTP post with the data needed
    for the tinderbox to be happy.
    """

    import random

    # the variables we will need to send on this form post
    variables =  {
        "tree"         : d.getVar('TINDER_TREE'),
        "machine_name" : d.getVar('TINDER_MACHINE'),
        "os"           : os.uname()[0],
        "os_version"   : os.uname()[2],
        "compiler"     : "gcc",
        "clobber"      : d.getVar('TINDER_CLOBBER') or "0",
        "srcdate"      : d.getVar('SRCDATE'),
        "PN"           : d.getVar('PN'),
        "PV"           : d.getVar('PV'),
        "PR"           : d.getVar('PR'),
        "FILE"         : d.getVar('FILE') or "N/A",
        "TARGETARCH"   : d.getVar('TARGET_ARCH'),
        "TARGETFPU"    : d.getVar('TARGET_FPU') or "Unknown",
        "TARGETOS"     : d.getVar('TARGET_OS') or "Unknown",
        "MACHINE"      : d.getVar('MACHINE') or "Unknown",
        "DISTRO"       : d.getVar('DISTRO') or "Unknown",
        "zecke-rocks"  : "sure",
    }

    # optionally add the status
    if status:
        variables["status"] = str(status)

    # try to load the machine id
    # we only need on build_status.pl but sending it
    # always does not hurt
    try:
        f = open(d.getVar('TMPDIR')+'/tinder-machine.id', 'r')
        id = f.read()
        variables['machine_id'] = id
    except:
        pass

    # the boundary we will need
    boundary = "----------------------------------%d" % int(random.random()*1000000000000)

    # now format the body
    body = tinder_form_data( boundary, variables, log )

    return ("multipart/form-data; boundary=%s" % boundary),body


def tinder_build_start(d):
    """
    Inform the tinderbox that a build is starting. We do this
    by posting our name and tree to the build_start.pl script
    on the server.
    """

    # get the body and type
    content_type, body = tinder_format_http_post(d,None,None)
    server = d.getVar('TINDER_HOST')
    url    = d.getVar('TINDER_URL')

    selector = url + "/xml/build_start.pl"

    #print("selector %s and url %s" % (selector, url))

    # now post it
    errcode, errmsg, headers, h_file = tinder_http_post(server,selector,content_type, body)
    #print(errcode, errmsg, headers)
    report = h_file.read()

    # now let us find the machine id that was assigned to us
    search = "<machine id='"
    report = report[report.find(search)+len(search):]
    report = report[0:report.find("'")]

    bb.note("Machine ID assigned by tinderbox: %s" % report )

    # now we will need to save the machine number
    # we will override any previous numbers
    f = open(d.getVar('TMPDIR')+"/tinder-machine.id", 'w')
    f.write(report)


def tinder_send_http(d, status, _log):
    """
    Send this log as build status
    """

    # get the body and type
    server = d.getVar('TINDER_HOST')
    url    = d.getVar('TINDER_URL')

    selector = url + "/xml/build_status.pl"

    # now post it - in chunks of 10.000 characters
    new_log = _log
    while len(new_log) > 0:
        content_type, body = tinder_format_http_post(d,status,new_log[0:18000])
        errcode, errmsg, headers, h_file = tinder_http_post(server,selector,content_type, body)
        #print(errcode, errmsg, headers)
        #print(h.file.read())
        new_log = new_log[18000:]


def tinder_print_info(d):
    """
    Print the TinderBox Info
        Including informations of the BaseSystem and the Tree
        we use.
    """

    # get the local vars
    time    = tinder_time_string()
    ops     = os.uname()[0]
    version = os.uname()[2]
    url     = d.getVar('TINDER_URL')
    tree    = d.getVar('TINDER_TREE')
    branch  = d.getVar('TINDER_BRANCH')
    srcdate = d.getVar('SRCDATE')
    machine = d.getVar('MACHINE')
    distro  = d.getVar('DISTRO')
    bbfiles = d.getVar('BBFILES')
    tarch   = d.getVar('TARGET_ARCH')
    fpu     = d.getVar('TARGET_FPU')
    oerev   = d.getVar('OE_REVISION') or "unknown"

    # there is a bug with tipple quoted strings
    # i will work around but will fix the original
    # bug as well
    output = []
    output.append("== Tinderbox Info" )
    output.append("Time: %(time)s" )
    output.append("OS: %(ops)s" )
    output.append("%(version)s" )
    output.append("Compiler: gcc" )
    output.append("Tinderbox Client: 0.1" )
    output.append("Tinderbox Client Last Modified: yesterday" )
    output.append("Tinderbox Protocol: 0.1" )
    output.append("URL: %(url)s" )
    output.append("Tree: %(tree)s" )
    output.append("Config:" )
    output.append("branch = '%(branch)s'" )
    output.append("TARGET_ARCH = '%(tarch)s'" )
    output.append("TARGET_FPU = '%(fpu)s'" )
    output.append("SRCDATE = '%(srcdate)s'" )
    output.append("MACHINE = '%(machine)s'" )
    output.append("DISTRO = '%(distro)s'" )
    output.append("BBFILES = '%(bbfiles)s'" )
    output.append("OEREV = '%(oerev)s'" )
    output.append("== End Tinderbox Client Info" )

    # now create the real output
    return "\n".join(output) % vars()


def tinder_print_env():
    """
    Print the environment variables of this build
    """
    time_start = tinder_time_string()
    time_end   = tinder_time_string()

    # build the environment
    env = ""
    for var in os.environ:
        env += "%s=%s\n" % (var, os.environ[var])

    output = []
    output.append( "---> TINDERBOX RUNNING env %(time_start)s" )
    output.append( env )
    output.append( "<--- TINDERBOX FINISHED (SUCCESS) %(time_end)s" )

    return "\n".join(output) % vars()

def tinder_tinder_start(d, event):
    """
    PRINT the configuration of this build
    """

    time_start = tinder_time_string()
    config = tinder_print_info(d)
    #env    = tinder_print_env()
    time_end   = tinder_time_string()
    packages = " ".join( event.getPkgs() ) 

    output = []
    output.append( "---> TINDERBOX PRINTING CONFIGURATION %(time_start)s" )
    output.append( config )
    #output.append( env    )
    output.append( "<--- TINDERBOX FINISHED PRINTING CONFIGURATION %(time_end)s" )
    output.append( "---> TINDERBOX BUILDING '%(packages)s'" )
    output.append( "<--- TINDERBOX STARTING BUILD NOW" )

    output.append( "" )

    return "\n".join(output) % vars()

def tinder_do_tinder_report(event):
    """
    Report to the tinderbox:
        On the BuildStart we will inform the box directly
        On the other events we will write to the TINDER_LOG and
        when the Task is finished we will send the report.

    The above is not yet fully implemented. Currently we send
    information immediately. The caching/queuing needs to be
    implemented. Also sending more or less information is not
    implemented yet.

    We have two temporary files stored in the TMP directory. One file
    contains the assigned machine id for the tinderclient. This id gets
    assigned when we connect the box and start the build process the second
    file is used to workaround an EventHandler limitation. If BitBake is ran
    with the continue option we want the Build to fail even if we get the
    BuildCompleted Event. In this case we have to look up the status and
    send it instead of 100/success.
    """
    import glob

    # variables
    name = bb.event.getName(event)
    log  = ""
    status = 1
    # Check what we need to do Build* shows we start or are done
    if name == "BuildStarted":
        tinder_build_start(event.data)
        log = tinder_tinder_start(event.data,event)

        try:
            # truncate the tinder log file
            f = open(event.data.getVar('TINDER_LOG'), 'w')
            f.write("")
            f.close()
        except:
            pass

        try:
            # write a status to the file. This is needed for the -k option
            # of BitBake
            g = open(event.data.getVar('TMPDIR')+"/tinder-status", 'w')
            g.write("")
            g.close()
        except IOError:
            pass

    # Append the Task-Log (compile,configure...) to the log file
    # we will send to the server
    if name == "TaskSucceeded" or name == "TaskFailed":
        log_file = glob.glob("%s/log.%s.*" % (event.data.getVar('T'), event.task))

        if len(log_file) != 0:
            to_file  = event.data.getVar('TINDER_LOG')
            log     += "".join(open(log_file[0], 'r').readlines())

    # set the right 'HEADER'/Summary for the TinderBox
    if name == "TaskStarted":
        log += "---> TINDERBOX Task %s started\n" % event.task
    elif name == "TaskSucceeded":
        log += "<--- TINDERBOX Task %s done (SUCCESS)\n" % event.task
    elif name == "TaskFailed":
        log += "<--- TINDERBOX Task %s failed (FAILURE)\n" % event.task
    elif name == "PkgStarted":
        log += "---> TINDERBOX Package %s started\n" % event.data.getVar('PF')
    elif name == "PkgSucceeded":
        log += "<--- TINDERBOX Package %s done (SUCCESS)\n" % event.data.getVar('PF')
    elif name == "PkgFailed":
        if not event.data.getVar('TINDER_AUTOBUILD') == "0":
            build.exec_task('do_clean', event.data)
        log += "<--- TINDERBOX Package %s failed (FAILURE)\n" % event.data.getVar('PF')
        status = 200
        # remember the failure for the -k case
        h = open(event.data.getVar('TMPDIR')+"/tinder-status", 'w')
        h.write("200")
    elif name == "BuildCompleted":
        log += "Build Completed\n"
        status = 100
        # Check if we have a old status...
        try:
            h = open(event.data.getVar('TMPDIR')+'/tinder-status', 'r')
            status = int(h.read())
        except:
            pass

    elif name == "MultipleProviders":
        log += "---> TINDERBOX Multiple Providers\n"
        log += "multiple providers are available (%s);\n" % ", ".join(event.getCandidates())
        log += "consider defining PREFERRED_PROVIDER_%s\n" % event.getItem()
        log += "is runtime: %d\n" % event.isRuntime()
        log += "<--- TINDERBOX Multiple Providers\n"
    elif name == "NoProvider":
        log += "Error: No Provider for: %s\n" % event.getItem()
        log += "Error:Was Runtime: %d\n" % event.isRuntime()
        status = 200
        # remember the failure for the -k case
        h = open(event.data.getVar('TMPDIR')+"/tinder-status", 'w')
        h.write("200")

    # now post the log
    if len(log) == 0:
        return

    # for now we will use the http post method as it is the only one
    log_post_method = tinder_send_http
    log_post_method(event.data, status, log)


# we want to be an event handler
addhandler tinderclient_eventhandler
python tinderclient_eventhandler() {
    if e.data is None or bb.event.getName(e) == "MsgNote":
        return

    do_tinder_report = e.data.getVar('TINDER_REPORT')
    if do_tinder_report and do_tinder_report == "1":
        tinder_do_tinder_report(e)

    return
}
