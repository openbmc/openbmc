# builtin.py - builtins and utilities definitions for pysh.
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

"""Builtin and internal utilities implementations.

- Beware not to use python interpreter environment as if it were the shell
environment. For instance, commands working directory must be explicitely handled
through env['PWD'] instead of relying on python working directory.
"""
import errno
import optparse
import os
import re
import subprocess
import sys
import time

def has_subprocess_bug():
    return getattr(subprocess, 'list2cmdline') and \
       (    subprocess.list2cmdline(['']) == '' or \
            subprocess.list2cmdline(['foo|bar']) == 'foo|bar')
            
# Detect python bug 1634343: "subprocess swallows empty arguments under win32"
# <http://sourceforge.net/tracker/index.php?func=detail&aid=1634343&group_id=5470&atid=105470>
# Also detect: "[ 1710802 ] subprocess must escape redirection characters under win32"
# <http://sourceforge.net/tracker/index.php?func=detail&aid=1710802&group_id=5470&atid=105470>
if has_subprocess_bug():
    import subprocess_fix
    subprocess.list2cmdline = subprocess_fix.list2cmdline

from sherrors import *

class NonExitingParser(optparse.OptionParser):
    """OptionParser default behaviour upon error is to print the error message and
    exit. Raise a utility error instead.
    """
    def error(self, msg):
        raise UtilityError(msg)

#-------------------------------------------------------------------------------  
# set special builtin
#-------------------------------------------------------------------------------  
OPT_SET = NonExitingParser(usage="set - set or unset options and positional parameters")
OPT_SET.add_option( '-f', action='store_true', dest='has_f', default=False,
    help='The shell shall disable pathname expansion.')
OPT_SET.add_option('-e', action='store_true', dest='has_e', default=False,
    help="""When this option is on, if a simple command fails for any of the \
    reasons listed in Consequences of Shell Errors or returns an exit status \
    value >0, and is not part of the compound list following a while, until, \
    or if keyword, and is not a part of an AND or OR list, and is not a \
    pipeline preceded by the ! reserved word, then the shell shall immediately \
    exit.""")
OPT_SET.add_option('-x', action='store_true', dest='has_x', default=False,
    help="""The shell shall write to standard error a trace for each command \
    after it expands the command and before it executes it. It is unspecified \
    whether the command that turns tracing off is traced.""")

def builtin_set(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    option, args = OPT_SET.parse_args(args)
    env = interp.get_env()
    
    if option.has_f:
        env.set_opt('-f')
    if option.has_e:
        env.set_opt('-e')
    if option.has_x:
        env.set_opt('-x')
    return 0
    
#-------------------------------------------------------------------------------  
# shift special builtin
#-------------------------------------------------------------------------------  
def builtin_shift(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    params = interp.get_env().get_positional_args()
    if args:
        try:
            n = int(args[0])
            if n > len(params):
                raise ValueError()
        except ValueError:
            return 1
    else:
        n = 1
        
    params[:n] = []
    interp.get_env().set_positional_args(params)
    return 0
    
#-------------------------------------------------------------------------------  
# export special builtin
#-------------------------------------------------------------------------------  
OPT_EXPORT = NonExitingParser(usage="set - set or unset options and positional parameters")
OPT_EXPORT.add_option('-p', action='store_true', dest='has_p', default=False)

def builtin_export(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    option, args = OPT_EXPORT.parse_args(args)
    if option.has_p:
        raise NotImplementedError()
    
    for arg in args:
        try:
            name, value = arg.split('=', 1)
        except ValueError:
            name, value = arg, None
        env = interp.get_env().export(name, value)
    
    return 0
    
#-------------------------------------------------------------------------------  
# return special builtin
#-------------------------------------------------------------------------------  
def builtin_return(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    res = 0
    if args:
        try:
            res = int(args[0])
        except ValueError:
            res = 0
        if not 0<=res<=255:
            res = 0
            
    # BUG: should be last executed command exit code        
    raise ReturnSignal(res)

#-------------------------------------------------------------------------------  
# trap special builtin
#-------------------------------------------------------------------------------  
def builtin_trap(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    if len(args) < 2:
        stderr.write('trap: usage: trap [[arg] signal_spec ...]\n')
        return 2

    action = args[0]
    for sig in args[1:]:
        try:
            env.traps[sig] = action
        except Exception as e:
            stderr.write('trap: %s\n' % str(e))
    return 0

#-------------------------------------------------------------------------------  
# unset special builtin
#-------------------------------------------------------------------------------
OPT_UNSET = NonExitingParser("unset - unset values and attributes of variables and functions")
OPT_UNSET.add_option( '-f', action='store_true', dest='has_f', default=False)
OPT_UNSET.add_option( '-v', action='store_true', dest='has_v', default=False)

def builtin_unset(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    option, args = OPT_UNSET.parse_args(args)
    
    status = 0
    env = interp.get_env()
    for arg in args:    
        try:
            if option.has_f:
                env.remove_function(arg)
            else:
                del env[arg]
        except KeyError:
            pass
        except VarAssignmentError:
            status = 1
            
    return status

#-------------------------------------------------------------------------------  
# wait special builtin
#-------------------------------------------------------------------------------  
def builtin_wait(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    return interp.wait([int(arg) for arg in args])

#-------------------------------------------------------------------------------  
# cat utility
#-------------------------------------------------------------------------------
def utility_cat(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    if not args:
        args = ['-']

    status = 0
    for arg in args:
        if arg == '-':
            data = stdin.read()
        else:
            path = os.path.join(env['PWD'], arg)
            try:
                f = file(path, 'rb')
                try:
                    data = f.read()
                finally:
                    f.close()
            except IOError as e:
                if e.errno != errno.ENOENT:
                    raise
                status = 1
                continue
        stdout.write(data)
        stdout.flush()
    return status
    
#-------------------------------------------------------------------------------  
# cd utility
#-------------------------------------------------------------------------------  
OPT_CD = NonExitingParser("cd - change the working directory")

def utility_cd(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    option, args = OPT_CD.parse_args(args)
    env = interp.get_env()
    
    directory = None
    printdir = False
    if not args:
        home = env.get('HOME')
        if home:
            # Unspecified, do nothing
            return 0
        else:
            directory = home
    elif len(args)==1:
        directory = args[0]
        if directory=='-':
            if 'OLDPWD' not in env:
                raise UtilityError("OLDPWD not set")
            printdir = True
            directory = env['OLDPWD']
    else:
        raise UtilityError("too many arguments")
            
    curpath = None
    # Absolute directories will be handled correctly by the os.path.join call.
    if not directory.startswith('.') and not directory.startswith('..'):
        cdpaths = env.get('CDPATH', '.').split(';')
        for cdpath in cdpaths:
            p = os.path.join(cdpath, directory)
            if os.path.isdir(p):
                curpath = p
                break
    
    if curpath is None:
        curpath = directory
    curpath = os.path.join(env['PWD'], directory)

    env['OLDPWD'] = env['PWD']
    env['PWD'] = curpath
    if printdir:
        stdout.write('%s\n' % curpath)
    return 0

#-------------------------------------------------------------------------------  
# colon utility
#-------------------------------------------------------------------------------  
def utility_colon(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    return 0
    
#-------------------------------------------------------------------------------  
# echo utility
#-------------------------------------------------------------------------------  
def utility_echo(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    # Echo only takes arguments, no options. Use printf if you need fancy stuff.
    output = ' '.join(args) + '\n'
    stdout.write(output)
    stdout.flush()
    return 0
    
#-------------------------------------------------------------------------------  
# egrep utility
#-------------------------------------------------------------------------------
# egrep is usually a shell script.
# Unfortunately, pysh does not support shell scripts *with arguments* right now,
# so the redirection is implemented here, assuming grep is available.
def utility_egrep(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    return run_command('grep', ['-E'] + args, interp, env, stdin, stdout, 
        stderr, debugflags)
    
#-------------------------------------------------------------------------------  
# env utility
#-------------------------------------------------------------------------------  
def utility_env(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    
    if args and args[0]=='-i':
        raise NotImplementedError('env: -i option is not implemented')
    
    i = 0
    for arg in args:
        if '=' not in arg:
            break
        # Update the current environment
        name, value = arg.split('=', 1)
        env[name] = value
        i += 1
        
    if args[i:]:
        # Find then execute the specified interpreter
        utility = env.find_in_path(args[i])
        if not utility:
            return 127
        args[i:i+1] = utility
        name = args[i]
        args = args[i+1:]
        try:
            return run_command(name, args, interp, env, stdin, stdout, stderr, 
                debugflags)
        except UtilityError:
            stderr.write('env: failed to execute %s' % ' '.join([name]+args))
            return 126            
    else:
        for pair in env.get_variables().iteritems():
            stdout.write('%s=%s\n' % pair)
    return 0
    
#-------------------------------------------------------------------------------  
# exit utility
#-------------------------------------------------------------------------------
def utility_exit(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    res = None
    if args:
        try:
            res = int(args[0])
        except ValueError:
            res = None
        if not 0<=res<=255:
            res = None
            
    if res is None:
        # BUG: should be last executed command exit code
        res = 0
        
    raise ExitSignal(res)

#-------------------------------------------------------------------------------  
# fgrep utility
#-------------------------------------------------------------------------------
# see egrep
def utility_fgrep(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    return run_command('grep', ['-F'] + args, interp, env, stdin, stdout, 
        stderr, debugflags)

#-------------------------------------------------------------------------------  
# gunzip utility
#-------------------------------------------------------------------------------
# see egrep
def utility_gunzip(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    return run_command('gzip', ['-d'] + args, interp, env, stdin, stdout, 
        stderr, debugflags)
    
#-------------------------------------------------------------------------------  
# kill utility
#-------------------------------------------------------------------------------
def utility_kill(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    for arg in args:
        pid = int(arg)
        status = subprocess.call(['pskill', '/T', str(pid)],
                                shell=True,
                                stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE)
        # pskill is asynchronous, hence the stupid polling loop
        while 1:
            p = subprocess.Popen(['pslist', str(pid)],
                                shell=True,
                                stdout=subprocess.PIPE,
                                stderr=subprocess.STDOUT)
            output = p.communicate()[0]
            if ('process %d was not' % pid) in output:
                break
            time.sleep(1)
    return status
    
#-------------------------------------------------------------------------------  
# mkdir utility
#-------------------------------------------------------------------------------
OPT_MKDIR = NonExitingParser("mkdir - make directories.")
OPT_MKDIR.add_option('-p', action='store_true', dest='has_p', default=False)

def utility_mkdir(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    # TODO: implement umask
    # TODO: implement proper utility error report
    option, args = OPT_MKDIR.parse_args(args)
    for arg in args:
        path = os.path.join(env['PWD'], arg)
        if option.has_p:
            try:
                os.makedirs(path)
            except IOError as e:
                if e.errno != errno.EEXIST:
                    raise
        else:               
            os.mkdir(path)
    return 0

#-------------------------------------------------------------------------------  
# netstat utility
#-------------------------------------------------------------------------------
def utility_netstat(name, args, interp, env, stdin, stdout, stderr, debugflags):
    # Do you really expect me to implement netstat ?
    # This empty form is enough for Mercurial tests since it's
    # supposed to generate nothing upon success. Faking this test
    # is not a big deal either.
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    return 0
    
#-------------------------------------------------------------------------------  
# pwd utility
#-------------------------------------------------------------------------------  
OPT_PWD = NonExitingParser("pwd - return working directory name")
OPT_PWD.add_option('-L', action='store_true', dest='has_L', default=True,
    help="""If the PWD environment variable contains an absolute pathname of \
    the current directory that does not contain the filenames dot or dot-dot, \
    pwd shall write this pathname to standard output. Otherwise, the -L option \
    shall behave as the -P option.""")
OPT_PWD.add_option('-P', action='store_true', dest='has_L', default=False,
    help="""The absolute pathname written shall not contain filenames that, in \
    the context of the pathname, refer to files of type symbolic link.""")

def utility_pwd(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    option, args = OPT_PWD.parse_args(args)        
    stdout.write('%s\n' % env['PWD'])
    return 0
    
#-------------------------------------------------------------------------------  
# printf utility
#-------------------------------------------------------------------------------
RE_UNESCAPE = re.compile(r'(\\x[a-zA-Z0-9]{2}|\\[0-7]{1,3}|\\.)')

def utility_printf(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    def replace(m):
        assert m.group()
        g = m.group()[1:]
        if g.startswith('x'):
            return chr(int(g[1:], 16))
        if len(g) <= 3 and len([c for c in g if c in '01234567']) == len(g):
            # Yay, an octal number
            return chr(int(g, 8))
        return {
            'a': '\a',
            'b': '\b',
            'f': '\f',
            'n': '\n',
            'r': '\r',
            't': '\t',
            'v': '\v',
            '\\': '\\',
        }.get(g)
        
    # Convert escape sequences
    format = re.sub(RE_UNESCAPE, replace, args[0])
    stdout.write(format % tuple(args[1:]))
    return 0
    
#-------------------------------------------------------------------------------  
# true utility
#-------------------------------------------------------------------------------
def utility_true(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    return 0

#-------------------------------------------------------------------------------  
# sed utility
#-------------------------------------------------------------------------------
RE_SED = re.compile(r'^s(.).*\1[a-zA-Z]*$')

# cygwin sed fails with some expressions when they do not end with a single space.
# see unit tests for details. Interestingly, the same expressions works perfectly
# in cygwin shell.
def utility_sed(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    # Scan pattern arguments and append a space if necessary
    for i in range(len(args)):
        if not RE_SED.search(args[i]):
            continue
        args[i] = args[i] + ' '

    return run_command(name, args, interp, env, stdin, stdout, 
        stderr, debugflags)

#-------------------------------------------------------------------------------  
# sleep utility
#-------------------------------------------------------------------------------
def utility_sleep(name, args, interp, env, stdin, stdout, stderr, debugflags):
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
    time.sleep(int(args[0]))
    return 0
    
#-------------------------------------------------------------------------------  
# sort utility
#-------------------------------------------------------------------------------
OPT_SORT = NonExitingParser("sort - sort, merge, or sequence check text files")

def utility_sort(name, args, interp, env, stdin, stdout, stderr, debugflags):

    def sort(path):
        if path == '-':
            lines = stdin.readlines()
        else:
            try:
                f = file(path)
                try:
                    lines = f.readlines()
                finally:
                    f.close()
            except IOError as e:
                stderr.write(str(e) + '\n')
                return 1
        
        if lines and lines[-1][-1]!='\n':
            lines[-1] = lines[-1] + '\n'
        return lines
    
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')
        
    option, args = OPT_SORT.parse_args(args)
    alllines = []
    
    if len(args)<=0:
        args += ['-']
        
    # Load all files lines
    curdir = os.getcwd()
    try:
        os.chdir(env['PWD'])
        for path in args:
            alllines += sort(path)
    finally:
        os.chdir(curdir)
            
    alllines.sort()
    for line in alllines:
        stdout.write(line)
    return 0
    
#-------------------------------------------------------------------------------
# hg utility
#-------------------------------------------------------------------------------

hgcommands = [
    'add',
    'addremove',
    'commit', 'ci',
    'debugrename',
    'debugwalk',
    'falabala', # Dummy command used in a mercurial test
    'incoming',
    'locate',
    'pull',
    'push',
    'qinit',
    'remove', 'rm',
    'rename', 'mv',
    'revert',    
    'showconfig',
    'status', 'st',
    'strip',
    ]

def rewriteslashes(name, args):
    # Several hg commands output file paths, rewrite the separators
    if len(args) > 1 and name.lower().endswith('python') \
       and args[0].endswith('hg'):
        for cmd in hgcommands:
            if cmd in args[1:]:
                return True
            
    # svn output contains many paths with OS specific separators.
    # Normalize these to unix paths.
    base = os.path.basename(name)
    if base.startswith('svn'):
        return True
    
    return False

def rewritehg(output):
    if not output:
        return output
    # Rewrite os specific messages
    output = output.replace(': The system cannot find the file specified',
                            ': No such file or directory')
    output = re.sub(': Access is denied.*$', ': Permission denied', output)
    output = output.replace(': No connection could be made because the target machine actively refused it',
                            ': Connection refused')
    return output
                            

def run_command(name, args, interp, env, stdin, stdout,
                stderr, debugflags):
    # Execute the command
    if 'debug-utility' in debugflags:
        print interp.log(' '.join([name, str(args), interp['PWD']]) + '\n')

    hgbin = interp.options().hgbinary
    ishg = hgbin and ('hg' in name or args and 'hg' in args[0])
    unixoutput = 'cygwin' in name or ishg
    
    exec_env = env.get_variables()        
    try:
        # BUG: comparing file descriptor is clearly not a reliable way to tell
        # whether they point on the same underlying object. But in pysh limited
        # scope this is usually right, we do not expect complicated redirections
        # besides usual 2>&1.
        # Still there is one case we have but cannot deal with is when stdout
        # and stderr are redirected *by pysh caller*. This the reason for the
        # --redirect pysh() option.
        # Now, we want to know they are the same because we sometimes need to 
        # transform the command output, mostly remove CR-LF to ensure that
        # command output is unix-like. Cygwin utilies are a special case because
        # they explicitely set their output streams to binary mode, so we have
        # nothing to do. For all others commands, we have to guess whether they
        # are sending text data, in which case the transformation must be done.
        # Again, the NUL character test is unreliable but should be enough for
        # hg tests.
        redirected = stdout.fileno()==stderr.fileno()
        if not redirected:
            p = subprocess.Popen([name] + args, cwd=env['PWD'], env=exec_env, 
                    stdin=stdin, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        else:
            p = subprocess.Popen([name] + args, cwd=env['PWD'], env=exec_env, 
                    stdin=stdin, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        out, err = p.communicate()
    except WindowsError as e:
        raise UtilityError(str(e))

    if not unixoutput:
        def encode(s):
            if '\0' in s:
                return s
            return s.replace('\r\n', '\n')
    else:
        encode = lambda s: s

    if rewriteslashes(name, args):
        encode1_ = encode
        def encode(s):
            s = encode1_(s)
            s = s.replace('\\\\', '\\')
            s = s.replace('\\', '/')
            return s

    if ishg:
        encode2_ = encode
        def encode(s):
            return rewritehg(encode2_(s))
    
    stdout.write(encode(out))
    if not redirected:
        stderr.write(encode(err))
    return p.returncode
            
