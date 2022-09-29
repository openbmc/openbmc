#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

OE_TERMINAL ?= 'auto'
OE_TERMINAL[type] = 'choice'
OE_TERMINAL[choices] = 'auto none \
                        ${@oe_terminal_prioritized()}'

OE_TERMINAL_EXPORTS += 'EXTRA_OEMAKE CACHED_CONFIGUREVARS CONFIGUREOPTS EXTRA_OECONF'
OE_TERMINAL_EXPORTS[type] = 'list'

XAUTHORITY ?= "${HOME}/.Xauthority"
SHELL ?= "bash"

def oe_terminal_prioritized():
    import oe.terminal
    return " ".join(o.name for o in oe.terminal.prioritized())

def emit_terminal_func(command, envdata, d):
    import bb.build
    cmd_func = 'do_terminal'

    envdata.setVar(cmd_func, 'exec ' + command)
    envdata.setVarFlag(cmd_func, 'func', '1')

    runfmt = d.getVar('BB_RUNFMT') or "run.{func}.{pid}"
    runfile = runfmt.format(func=cmd_func, task=cmd_func, taskfunc=cmd_func, pid=os.getpid())
    runfile = os.path.join(d.getVar('T'), runfile)
    bb.utils.mkdirhier(os.path.dirname(runfile))

    with open(runfile, 'w') as script:
        # Override the shell shell_trap_code specifies.
        # If our shell is bash, we might well face silent death.
        script.write("#!/bin/bash\n")
        script.write(bb.build.shell_trap_code())
        bb.data.emit_func(cmd_func, script, envdata)
        script.write(cmd_func)
        script.write("\n")
    os.chmod(runfile, 0o755)

    return runfile

def oe_terminal(command, title, d):
    import oe.data
    import oe.terminal
    
    envdata = bb.data.init()

    for v in os.environ:
        envdata.setVar(v, os.environ[v])
        envdata.setVarFlag(v, 'export', '1')

    for export in oe.data.typed_value('OE_TERMINAL_EXPORTS', d):
        value = d.getVar(export)
        if value is not None:
            os.environ[export] = str(value)
            envdata.setVar(export, str(value))
            envdata.setVarFlag(export, 'export', '1')
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
        value = origbbenv.getVar(key)
        if value is not None:
            os.environ[key] = str(value)
            envdata.setVar(key, str(value))
            envdata.setVarFlag(key, 'export', '1')

    # Use original PATH as a fallback
    path = d.getVar('PATH') + ":" + origbbenv.getVar('PATH')
    os.environ['PATH'] = path
    envdata.setVar('PATH', path)

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
    except oe.terminal.NoSupportedTerminals as nosup:
        nosup.terms.remove("false")
        cmds = '\n\t'.join(nosup.terms).replace("{command}",
                    "do_terminal").replace("{title}", title)
        bb.fatal('No valid terminal found, unable to open devshell.\n' +
                'Tried the following commands:\n\t%s' % cmds)
    except oe.terminal.ExecutionError as exc:
        bb.fatal('Unable to spawn terminal %s: %s' % (terminal, exc))

oe_terminal[vardepsexclude] = "BB_ORIGENV"
