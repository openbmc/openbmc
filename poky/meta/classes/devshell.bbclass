inherit terminal

DEVSHELL = "${SHELL}"

python do_devshell () {
    if d.getVarFlag("do_devshell", "manualfakeroot"):
       d.prependVar("DEVSHELL", "pseudo ")
       fakeenv = d.getVar("FAKEROOTENV").split()
       for f in fakeenv:
            k = f.split("=")
            d.setVar(k[0], k[1])
            d.appendVar("OE_TERMINAL_EXPORTS", " " + k[0])
       d.delVarFlag("do_devshell", "fakeroot")

    oe_terminal(d.getVar('DEVSHELL'), 'OpenEmbedded Developer Shell', d)
}

addtask devshell after do_patch do_prepare_recipe_sysroot

# The directory that the terminal starts in
DEVSHELL_STARTDIR ?= "${S}"
do_devshell[dirs] = "${DEVSHELL_STARTDIR}"
do_devshell[nostamp] = "1"

# devshell and fakeroot/pseudo need careful handling since only the final
# command should run under fakeroot emulation, any X connection should
# be done as the normal user. We therfore carefully construct the envionment
# manually
python () {
    if d.getVarFlag("do_devshell", "fakeroot"):
       # We need to signal our code that we want fakeroot however we
       # can't manipulate the environment and variables here yet (see YOCTO #4795)
       d.setVarFlag("do_devshell", "manualfakeroot", "1")
       d.delVarFlag("do_devshell", "fakeroot")
} 

def devpyshell(d):

    import code
    import select
    import signal
    import termios

    m, s = os.openpty()
    sname = os.ttyname(s)

    def noechoicanon(fd):
        old = termios.tcgetattr(fd)
        old[3] = old[3] &~ termios.ECHO &~ termios.ICANON
        # &~ termios.ISIG
        termios.tcsetattr(fd, termios.TCSADRAIN, old)

    # No echo or buffering over the pty
    noechoicanon(s)

    pid = os.fork()
    if pid:
        os.close(m)
        oe_terminal("oepydevshell-internal.py %s %d" % (sname, pid), 'OpenEmbedded Developer PyShell', d)
        os._exit(0)
    else:
        os.close(s)

        os.dup2(m, sys.stdin.fileno())
        os.dup2(m, sys.stdout.fileno())
        os.dup2(m, sys.stderr.fileno())

        bb.utils.nonblockingfd(sys.stdout)
        bb.utils.nonblockingfd(sys.stderr)
        bb.utils.nonblockingfd(sys.stdin)

        _context = {
            "os": os,
            "bb": bb,
            "time": time,
            "d": d,
        }

        ps1 = "pydevshell> "
        ps2 = "... "
        buf = []
        more = False

        i = code.InteractiveInterpreter(locals=_context)
        print("OE PyShell (PN = %s)\n" % d.getVar("PN"))

        def prompt(more):
            if more:
                prompt = ps2
            else:
                prompt = ps1
            sys.stdout.write(prompt)
            sys.stdout.flush()

        # Restore Ctrl+C since bitbake masks this
        def signal_handler(signal, frame):
            raise KeyboardInterrupt
        signal.signal(signal.SIGINT, signal_handler)

        child = None

        prompt(more)
        while True:
            try:
                try:
                    (r, _, _) = select.select([sys.stdin], [], [], 1)
                    if not r:
                        continue
                    line = sys.stdin.readline().strip()
                    if not line:
                        prompt(more)
                        continue
                except EOFError as e:
                    sys.stdout.write("\n")
                    sys.stdout.flush()
                except (OSError, IOError) as e:
                    if e.errno == 11:
                        continue
                    if e.errno == 5:
                        return
                    raise
                else:
                    if not child:
                        child = int(line)
                        continue
                    buf.append(line)
                    source = "\n".join(buf)
                    more = i.runsource(source, "<pyshell>")
                    if not more:
                        buf = []
                    sys.stderr.flush()
                    prompt(more)
            except KeyboardInterrupt:
                i.write("\nKeyboardInterrupt\n")
                buf = []
                more = False
                prompt(more)
            except SystemExit:
                # Easiest way to ensure everything exits
                os.kill(child, signal.SIGTERM)
                break

python do_devpyshell() {
    import signal

    try:
        devpyshell(d)
    except SystemExit:
        # Stop the SIGTERM above causing an error exit code
        return
    finally:
        return
}
addtask devpyshell after do_patch

do_devpyshell[nostamp] = "1"
