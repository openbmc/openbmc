OE_TERMINAL ?= 'auto'
OE_TERMINAL[type] = 'choice'
OE_TERMINAL[choices] = 'auto none \
                        ${@" ".join(o.name \
                                    for o in oe.terminal.prioritized())}'

OE_TERMINAL_EXPORTS += 'EXTRA_OEMAKE'
OE_TERMINAL_EXPORTS[type] = 'list'

XAUTHORITY ?= "${HOME}/.Xauthority"
SHELL ?= "bash"


def emit_terminal_func(command, envdata, d):
    cmd_func = 'do_terminal'

    envdata.setVar(cmd_func, 'exec ' + command)
    envdata.setVarFlag(cmd_func, 'func', 1)

    runfmt = d.getVar('BB_RUNFMT', True) or "run.{func}.{pid}"
    runfile = runfmt.format(func=cmd_func, task=cmd_func, taskfunc=cmd_func, pid=os.getpid())
    runfile = os.path.join(d.getVar('T', True), runfile)
    bb.utils.mkdirhier(os.path.dirname(runfile))

    with open(runfile, 'w') as script:
        script.write('#!/bin/sh -e\n')
        bb.data.emit_func(cmd_func, script, envdata)
        script.write(cmd_func)
        script.write("\n")
    os.chmod(runfile, 0755)

    return runfile

def oe_terminal(command, title, d):
    import oe.data
    import oe.terminal

    envdata = bb.data.init()

    for v in os.environ:
        envdata.setVar(v, os.environ[v])
        envdata.setVarFlag(v, 'export', 1)

    for export in oe.data.typed_value('OE_TERMINAL_EXPORTS', d):
        value = d.getVar(export, True)
        if value is not None:
            os.environ[export] = str(value)
            envdata.setVar(export, str(value))
            envdata.setVarFlag(export, 'export', 1)
        if export == "PSEUDO_DISABLED":
            if "PSEUDO_UNLOAD" in os.environ:
                del os.environ["PSEUDO_UNLOAD"]
            envdata.delVar("PSEUDO_UNLOAD")

    # Add in all variables from the user's original environment which
    # haven't subsequntly been set/changed
    origbbenv = d.getVar("BB_ORIGENV", False) or {}
    for key in origbbenv:
        if key in envdata:
            continue
        value = origbbenv.getVar(key, True)
        if value is not None:
            os.environ[key] = str(value)
            envdata.setVar(key, str(value))
            envdata.setVarFlag(key, 'export', 1)

    # A complex PS1 might need more escaping of chars.
    # Lets not export PS1 instead.
    envdata.delVar("PS1")

    # Replace command with an executable wrapper script
    command = emit_terminal_func(command, envdata, d)

    terminal = oe.data.typed_value('OE_TERMINAL', d).lower()
    if terminal == 'none':
        bb.fatal('Devshell usage disabled with OE_TERMINAL')
    elif terminal != 'auto':
        try:
            oe.terminal.spawn(terminal, command, title, None, d)
            return
        except oe.terminal.UnsupportedTerminal:
            bb.warn('Unsupported terminal "%s", defaulting to "auto"' %
                    terminal)
        except oe.terminal.ExecutionError as exc:
            bb.fatal('Unable to spawn terminal %s: %s' % (terminal, exc))

    try:
        oe.terminal.spawn_preferred(command, title, None, d)
    except oe.terminal.NoSupportedTerminals:
        bb.fatal('No valid terminal found, unable to open devshell')
    except oe.terminal.ExecutionError as exc:
        bb.fatal('Unable to spawn terminal %s: %s' % (terminal, exc))

oe_terminal[vardepsexclude] = "BB_ORIGENV"
