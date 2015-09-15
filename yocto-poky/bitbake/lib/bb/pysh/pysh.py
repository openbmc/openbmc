# pysh.py - command processing for pysh.
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

import optparse
import os
import sys

import interp

SH_OPT = optparse.OptionParser(prog='pysh', usage="%prog [OPTIONS]", version='0.1')
SH_OPT.add_option('-c', action='store_true', dest='command_string', default=None, 
    help='A string that shall be interpreted by the shell as one or more commands')
SH_OPT.add_option('--redirect-to', dest='redirect_to', default=None, 
    help='Redirect script commands stdout and stderr to the specified file')
# See utility_command in builtin.py about the reason for this flag.
SH_OPT.add_option('--redirected', dest='redirected', action='store_true', default=False, 
    help='Tell the interpreter that stdout and stderr are actually the same objects, which is really stdout')
SH_OPT.add_option('--debug-parsing', action='store_true', dest='debug_parsing', default=False, 
    help='Trace PLY execution')
SH_OPT.add_option('--debug-tree', action='store_true', dest='debug_tree', default=False, 
    help='Display the generated syntax tree.')
SH_OPT.add_option('--debug-cmd', action='store_true', dest='debug_cmd', default=False, 
    help='Trace command execution before parameters expansion and exit status.')
SH_OPT.add_option('--debug-utility', action='store_true', dest='debug_utility', default=False, 
    help='Trace utility calls, after parameters expansions')
SH_OPT.add_option('--ast', action='store_true', dest='ast', default=False,
    help='Encoded commands to execute in a subprocess')
SH_OPT.add_option('--profile', action='store_true', default=False,
    help='Profile pysh run')
    
    
def split_args(args):
    # Separate shell arguments from command ones
    # Just stop at the first argument not starting with a dash. I know, this is completely broken,
    # it ignores files starting with a dash or may take option values for command file. This is not
    # supposed to happen for now
    command_index = len(args)
    for i,arg in enumerate(args):
        if not arg.startswith('-'):
            command_index = i
            break
            
    return args[:command_index], args[command_index:]


def fixenv(env):
    path = env.get('PATH')
    if path is not None:
        parts = path.split(os.pathsep)
        # Remove Windows utilities from PATH, they are useless at best and
        # some of them (find) may be confused with other utilities.
        parts = [p for p in parts if 'system32' not in p.lower()]
        env['PATH'] = os.pathsep.join(parts)
    if env.get('HOME') is None:
        # Several utilities, including cvsps, cannot work without
        # a defined HOME directory.
        env['HOME'] = os.path.expanduser('~')
    return env

def _sh(cwd, shargs, cmdargs, options, debugflags=None, env=None):
    if os.environ.get('PYSH_TEXT') != '1':
        import msvcrt
        for fp in (sys.stdin, sys.stdout, sys.stderr):
            msvcrt.setmode(fp.fileno(), os.O_BINARY)

    hgbin = os.environ.get('PYSH_HGTEXT') != '1'
    
    if debugflags is None:
        debugflags = []
        if options.debug_parsing:    debugflags.append('debug-parsing')
        if options.debug_utility:    debugflags.append('debug-utility')
        if options.debug_cmd:        debugflags.append('debug-cmd')
        if options.debug_tree:       debugflags.append('debug-tree')
    
    if env is None:
        env = fixenv(dict(os.environ))
    if cwd is None:
        cwd = os.getcwd()

    if not cmdargs:
        # Nothing to do
        return 0

    ast = None
    command_file = None
    if options.command_string:
        input = cmdargs[0]
        if not options.ast:
            input += '\n'
        else:
            args, input = interp.decodeargs(input), None
            env, ast = args
            cwd = env.get('PWD', cwd)
    else:
        command_file = cmdargs[0]
        arguments = cmdargs[1:]

        prefix = interp.resolve_shebang(command_file, ignoreshell=True)
        if prefix:
            input = ' '.join(prefix + [command_file] + arguments)
        else:
            # Read commands from file
            f = file(command_file)
            try:
                # Trailing newline to help the parser
                input = f.read() + '\n'
            finally:
                f.close()
    
    redirect = None
    try:
        if options.redirected:
            stdout = sys.stdout
            stderr = stdout
        elif options.redirect_to:
            redirect = open(options.redirect_to, 'wb')
            stdout = redirect
            stderr = redirect
        else:
            stdout = sys.stdout
            stderr = sys.stderr
            
        # TODO: set arguments to environment variables
        opts = interp.Options()
        opts.hgbinary = hgbin
        ip = interp.Interpreter(cwd, debugflags, stdout=stdout, stderr=stderr,
                                opts=opts)
        try:
            # Export given environment in shell object
            for k,v in env.iteritems():
                ip.get_env().export(k,v)
            return ip.execute_script(input, ast, scriptpath=command_file)
        finally:
            ip.close()
    finally:
        if redirect is not None:
            redirect.close()

def sh(cwd=None, args=None, debugflags=None, env=None):
    if args is None:
        args = sys.argv[1:]
    shargs, cmdargs = split_args(args)
    options, shargs = SH_OPT.parse_args(shargs)

    if options.profile:
        import lsprof
        p = lsprof.Profiler()
        p.enable(subcalls=True)
        try:
            return _sh(cwd, shargs, cmdargs, options, debugflags, env)
        finally:
            p.disable()
            stats = lsprof.Stats(p.getstats())
            stats.sort()
            stats.pprint(top=10, file=sys.stderr, climit=5)
    else:
        return _sh(cwd, shargs, cmdargs, options, debugflags, env)
            
def main():
    sys.exit(sh())

if __name__=='__main__':
    main()
