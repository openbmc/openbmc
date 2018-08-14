# interp.py - shell interpreter for pysh.
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

"""Implement the shell interpreter.

Most references are made to "The Open Group Base Specifications Issue 6".
<http://www.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html>
"""
# TODO: document the fact input streams must implement fileno() so Popen will work correctly.
# it requires non-stdin stream to be implemented as files. Still to be tested...
# DOC: pathsep is used in PATH instead of ':'. Clearly, there are path syntax issues here.
# TODO: stop command execution upon error.
# TODO: sort out the filename/io_number mess. It should be possible to use filenames only.
# TODO: review subshell implementation
# TODO: test environment cloning for non-special builtins
# TODO: set -x should not rebuild commands from tokens, assignments/redirections are lost
# TODO: unit test for variable assignment
# TODO: test error management wrt error type/utility type
# TODO: test for binary output everywhere
# BUG: debug-parsing does not pass log file to PLY. Maybe a PLY upgrade is necessary.
import base64
import cPickle as pickle
import errno
import glob
import os
import re
import subprocess
import sys
import tempfile

try:
    s = set()
    del s
except NameError:
    from Set import Set as set

import builtin
from sherrors import *
import pyshlex
import pyshyacc

def mappend(func, *args, **kargs):
    """Like map but assume func returns a list. Returned lists are merged into
    a single one.
    """
    return reduce(lambda a,b: a+b, map(func, *args, **kargs), [])

class FileWrapper:
    """File object wrapper to ease debugging.
    
    Allow mode checking and implement file duplication through a simple 
    reference counting scheme. Not sure the latter is really useful since
    only real file descriptors can be used.
    """
    def __init__(self, mode, file, close=True):
        if mode not in ('r', 'w', 'a'):
            raise IOError('invalid mode: %s' % mode)
        self._mode = mode
        self._close = close
        if isinstance(file, FileWrapper):
            if file._refcount[0] <= 0:
                raise IOError(0, 'Error')
            self._refcount = file._refcount
            self._refcount[0] += 1
            self._file = file._file
        else:
            self._refcount = [1]
            self._file = file
        
    def dup(self):
        return FileWrapper(self._mode, self, self._close)
        
    def fileno(self):
        """fileno() should be only necessary for input streams."""
        return self._file.fileno()
        
    def read(self, size=-1):
        if self._mode!='r':
            raise IOError(0, 'Error')
        return self._file.read(size)
        
    def readlines(self, *args, **kwargs):
        return self._file.readlines(*args, **kwargs)
        
    def write(self, s):
        if self._mode not in ('w', 'a'):
            raise IOError(0, 'Error')
        return self._file.write(s)
        
    def flush(self):
        self._file.flush()
        
    def close(self):        
        if not self._refcount:
            return
        assert  self._refcount[0] > 0
        
        self._refcount[0] -= 1    
        if self._refcount[0] == 0:
            self._mode = 'c'
            if self._close:
                self._file.close()
        self._refcount = None
                
    def mode(self):
        return self._mode

    def __getattr__(self, name):
        if name == 'name':
            self.name = getattr(self._file, name)
            return self.name
        else:
            raise AttributeError(name)

    def __del__(self):
        self.close()
               
               
def win32_open_devnull(mode):
    return open('NUL', mode)
    
        
class Redirections:
    """Stores open files and their mapping to pseudo-sh file descriptor.
    """
    # BUG: redirections are not handled correctly: 1>&3 2>&3 3>&4 does 
    # not make 1 to redirect to 4
    def __init__(self, stdin=None, stdout=None, stderr=None):
        self._descriptors = {}
        if stdin is not None:
            self._add_descriptor(0, stdin)
        if stdout is not None:
            self._add_descriptor(1, stdout)
        if stderr is not None:
            self._add_descriptor(2, stderr)
            
    def add_here_document(self, interp, name, content, io_number=None):
        if io_number is None:
            io_number = 0
            
        if name==pyshlex.unquote_wordtree(name):
            content = interp.expand_here_document(('TOKEN', content))
    
        # Write document content in a temporary file
        tmp = tempfile.TemporaryFile()
        try:
            tmp.write(content)
            tmp.flush()
            tmp.seek(0)
            self._add_descriptor(io_number, FileWrapper('r', tmp))
        except:
            tmp.close()
            raise                
        
    def add(self, interp, op, filename, io_number=None):
        if op not in ('<', '>', '>|', '>>', '>&'):
            # TODO: add descriptor duplication and here_documents      
            raise RedirectionError('Unsupported redirection operator "%s"' % op)
            
        if io_number is not None:
            io_number = int(io_number)
            
        if (op == '>&' and filename.isdigit()) or filename=='-':
            # No expansion for file descriptors, quote them if you want a filename
            fullname = filename
        else:
            if filename.startswith('/'):
                # TODO: win32 kludge
                if filename=='/dev/null':
                    fullname = 'NUL'
                else:
                    # TODO: handle absolute pathnames, they are unlikely to exist on the
                    # current platform (win32 for instance).
                    raise NotImplementedError()
            else:
                fullname = interp.expand_redirection(('TOKEN', filename))
                if not fullname:
                    raise RedirectionError('%s: ambiguous redirect' % filename)
                # Build absolute path based on PWD
                fullname = os.path.join(interp.get_env()['PWD'], fullname)
            
        if op=='<':
            return self._add_input_redirection(interp, fullname, io_number)
        elif op in ('>', '>|'):
            clobber = ('>|'==op)
            return self._add_output_redirection(interp, fullname, io_number, clobber)
        elif op=='>>':
            return self._add_output_appending(interp, fullname, io_number)
        elif op=='>&':
            return self._dup_output_descriptor(fullname, io_number)
        
    def close(self):
        if self._descriptors is not None:
            for desc in self._descriptors.itervalues():
                desc.flush()
                desc.close()
            self._descriptors = None
            
    def stdin(self):
        return self._descriptors[0]
          
    def stdout(self):
        return self._descriptors[1] 
        
    def stderr(self):
        return self._descriptors[2] 
            
    def clone(self):
        clone = Redirections()
        for desc, fileobj in self._descriptors.iteritems():
            clone._descriptors[desc] = fileobj.dup()
        return clone
           
    def _add_output_redirection(self, interp, filename, io_number, clobber):    
        if io_number is None:
            # io_number default to standard output
            io_number = 1
        
        if not clobber and interp.get_env().has_opt('-C') and os.path.isfile(filename):
            # File already exist in no-clobber mode, bail out
            raise RedirectionError('File "%s" already exists' % filename)
            
        # Open and register
        self._add_file_descriptor(io_number, filename, 'w')
        
    def _add_output_appending(self, interp, filename, io_number):    
        if io_number is None:
            io_number = 1
        self._add_file_descriptor(io_number, filename, 'a')
            
    def _add_input_redirection(self, interp, filename, io_number):
        if io_number is None:
            io_number = 0
        self._add_file_descriptor(io_number, filename, 'r')
        
    def _add_file_descriptor(self, io_number, filename, mode):    
        try:            
            if filename.startswith('/'):
                if filename=='/dev/null':
                    f = win32_open_devnull(mode+'b')
                else:
                    # TODO: handle absolute pathnames, they are unlikely to exist on the
                    # current platform (win32 for instance).
                    raise NotImplementedError('cannot open absolute path %s' % repr(filename))
            else:
                f = file(filename, mode+'b')
        except IOError as e:
            raise RedirectionError(str(e))
            
        wrapper = None
        try:
            wrapper = FileWrapper(mode, f)
            f = None
            self._add_descriptor(io_number, wrapper)
        except:
            if f: f.close()
            if wrapper: wrapper.close()
            raise
            
    def _dup_output_descriptor(self, source_fd, dest_fd):
        if source_fd is None:
            source_fd = 1
        self._dup_file_descriptor(source_fd, dest_fd, 'w')
            
    def _dup_file_descriptor(self, source_fd, dest_fd, mode):
        source_fd = int(source_fd)
        if source_fd not in self._descriptors:
            raise RedirectionError('"%s" is not a valid file descriptor' % str(source_fd))
        source = self._descriptors[source_fd]
        
        if source.mode()!=mode:
            raise RedirectionError('Descriptor %s cannot be duplicated in mode "%s"' % (str(source), mode))
        
        if dest_fd=='-':
            # Close the source descriptor
            del self._descriptors[source_fd]
            source.close()
        else:
            dest_fd = int(dest_fd)
            if dest_fd not in self._descriptors:
                raise RedirectionError('Cannot replace file descriptor %s' % str(dest_fd))
                
            dest = self._descriptors[dest_fd]
            if dest.mode()!=mode:
                raise RedirectionError('Descriptor %s cannot be cannot be redirected in mode "%s"' % (str(dest), mode))
            
            self._descriptors[dest_fd] = source.dup()
            dest.close()        
            
    def _add_descriptor(self, io_number, file):
        io_number = int(io_number)
        
        if io_number in self._descriptors:
            # Close the current descriptor
            d = self._descriptors[io_number]
            del self._descriptors[io_number]
            d.close()
            
        self._descriptors[io_number] = file

    def __str__(self):
        names = [('%d=%r' % (k, getattr(v, 'name', None))) for k,v
                 in self._descriptors.iteritems()]
        names = ','.join(names)
        return 'Redirections(%s)' % names

    def __del__(self):
        self.close()
    
def cygwin_to_windows_path(path):
    """Turn /cygdrive/c/foo into c:/foo, or return path if it
    is not a cygwin path.
    """
    if not path.startswith('/cygdrive/'):
        return path
    path = path[len('/cygdrive/'):]
    path = path[:1] + ':' + path[1:]
    return path
    
def win32_to_unix_path(path):
    if path is not None:
        path = path.replace('\\', '/')
    return path    
    
_RE_SHEBANG = re.compile(r'^\#!\s?([^\s]+)(?:\s([^\s]+))?')
_SHEBANG_CMDS = {
    '/usr/bin/env': 'env',
    '/bin/sh': 'pysh',
    'python': 'python',
}
    
def resolve_shebang(path, ignoreshell=False):
    """Return a list of arguments as shebang interpreter call or an empty list
    if path does not refer to an executable script.
    See <http://www.opengroup.org/austin/docs/austin_51r2.txt>.
    
    ignoreshell - set to True to ignore sh shebangs. Return an empty list instead.
    """
    try:
        f = file(path)
        try:
            # At most 80 characters in the first line
            header = f.read(80).splitlines()[0]
        finally:
            f.close()
            
        m = _RE_SHEBANG.search(header)
        if not m:
            return []
        cmd, arg = m.group(1,2)
        if os.path.isfile(cmd):
            # Keep this one, the hg script for instance contains a weird windows
            # shebang referencing the current python install.
            cmdfile = os.path.basename(cmd).lower()
            if cmdfile == 'python.exe':
                cmd = 'python'
            pass
        elif cmd not in _SHEBANG_CMDS:
            raise CommandNotFound('Unknown interpreter "%s" referenced in '\
                'shebang' % header)
        cmd = _SHEBANG_CMDS.get(cmd)
        if cmd is None or (ignoreshell and cmd == 'pysh'):
            return []
        if arg is None:
            return [cmd, win32_to_unix_path(path)]
        return [cmd, arg, win32_to_unix_path(path)]
    except IOError as e:
        if  e.errno!=errno.ENOENT and \
            (e.errno!=errno.EPERM and not os.path.isdir(path)): # Opening a directory raises EPERM
            raise
        return []
        
def win32_find_in_path(name, path):
    if isinstance(path, str):
        path = path.split(os.pathsep)
        
    exts = os.environ.get('PATHEXT', '').lower().split(os.pathsep)
    for p in path:
        p_name = os.path.join(p, name)
        
        prefix = resolve_shebang(p_name)
        if prefix:
            return prefix
            
        for ext in exts:    
            p_name_ext = p_name + ext
            if os.path.exists(p_name_ext):
                return [win32_to_unix_path(p_name_ext)]
    return []

class Traps(dict):
    def __setitem__(self, key, value):
        if key not in ('EXIT',):
            raise NotImplementedError()
        super(Traps, self).__setitem__(key, value)

# IFS white spaces character class
_IFS_WHITESPACES = (' ', '\t', '\n')

class Environment:
    """Environment holds environment variables, export table, function 
    definitions and whatever is defined in 2.12 "Shell Execution Environment",
    redirection excepted.
    """
    def __init__(self, pwd):
        self._opt = set()       #Shell options
        
        self._functions = {}        
        self._env = {'?': '0', '#': '0'}
        self._exported = set([
            'HOME', 'IFS', 'PATH'
        ])
        
        # Set environment vars with side-effects
        self._ifs_ws = None     # Set of IFS whitespace characters
        self._ifs_re = None     # Regular expression used to split between words using IFS classes
        self['IFS'] = ''.join(_IFS_WHITESPACES) #Default environment values
        self['PWD'] = pwd
        self.traps = Traps()
        
    def clone(self, subshell=False):
        env = Environment(self['PWD'])
        env._opt = set(self._opt)
        for k,v in self.get_variables().iteritems():
            if k in self._exported:
                env.export(k,v)
            elif subshell:
                env[k] = v
                
        if subshell:
            env._functions = dict(self._functions)
            
        return env        
        
    def __getitem__(self, key):
        if key in ('@', '*', '-', '$'):
            raise NotImplementedError('%s is not implemented' % repr(key))
        return self._env[key]
        
    def get(self, key, defval=None):
        try:
            return self[key]
        except KeyError:
            return defval
        
    def __setitem__(self, key, value):
        if key=='IFS':
            # Update the whitespace/non-whitespace classes
            self._update_ifs(value)
        elif key=='PWD':
            pwd = os.path.abspath(value)
            if not os.path.isdir(pwd):
                raise VarAssignmentError('Invalid directory %s' % value)
            value = pwd
        elif key in ('?', '!'):
            value = str(int(value))
        self._env[key] = value
        
    def __delitem__(self, key):
        if key in ('IFS', 'PWD', '?'):
            raise VarAssignmentError('%s cannot be unset' % key)
        del self._env[key]

    def __contains__(self, item):
        return item in self._env
        
    def set_positional_args(self, args):
        """Set the content of 'args' as positional argument from 1 to len(args).
        Return previous argument as a list of strings.
        """
        # Save and remove previous arguments
        prevargs = []        
        for i in range(int(self._env['#'])):
            i = str(i+1)
            prevargs.append(self._env[i])
            del self._env[i]
        self._env['#'] = '0'
                
        #Set new ones
        for i,arg in enumerate(args):
            self._env[str(i+1)] = str(arg)
        self._env['#'] = str(len(args))
        
        return prevargs
        
    def get_positional_args(self):
        return [self._env[str(i+1)] for i in range(int(self._env['#']))]
        
    def get_variables(self):
        return dict(self._env)
        
    def export(self, key, value=None):
        if value is not None:
            self[key] = value
        self._exported.add(key)
        
    def get_exported(self):
        return [(k,self._env.get(k)) for k in self._exported]
            
    def split_fields(self, word):
        if not self._ifs_ws or not word:
            return [word]
        return re.split(self._ifs_re, word)
   
    def _update_ifs(self, value):
        """Update the split_fields related variables when IFS character set is
        changed.
        """
        # TODO: handle NULL IFS
        
        # Separate characters in whitespace and non-whitespace
        chars = set(value)
        ws = [c for c in chars if c in _IFS_WHITESPACES]
        nws = [c for c in chars if c not in _IFS_WHITESPACES]
        
        # Keep whitespaces in a string for left and right stripping
        self._ifs_ws = ''.join(ws)
        
        # Build a regexp to split fields
        trailing = '[' + ''.join([re.escape(c) for c in ws]) + ']'
        if nws:
            # First, the single non-whitespace occurence.
            nws = '[' + ''.join([re.escape(c) for c in nws]) + ']'
            nws = '(?:' + trailing + '*' + nws + trailing + '*' + '|' + trailing + '+)'
        else:
            # Then mix all parts with quantifiers
            nws = trailing + '+'
        self._ifs_re = re.compile(nws)
       
    def has_opt(self, opt, val=None):
        return (opt, val) in self._opt
        
    def set_opt(self, opt, val=None):
        self._opt.add((opt, val))
        
    def find_in_path(self, name, pwd=False):
        path = self._env.get('PATH', '').split(os.pathsep)
        if pwd:
            path[:0] = [self['PWD']]
        if os.name == 'nt':
            return win32_find_in_path(name, self._env.get('PATH', ''))
        else:
            raise NotImplementedError()
            
    def define_function(self, name, body):
        if not is_name(name):
            raise ShellSyntaxError('%s is not a valid function name' % repr(name))
        self._functions[name] = body
        
    def remove_function(self, name):
        del self._functions[name]
        
    def is_function(self, name):
        return name in self._functions
        
    def get_function(self, name):
        return self._functions.get(name)
        
       
name_charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_'
name_charset = dict(zip(name_charset,name_charset))
           
def match_name(s):
    """Return the length in characters of the longest prefix made of name
    allowed characters in s.
    """
    for i,c in enumerate(s):
        if c not in name_charset:
            return s[:i]
    return s
    
def is_name(s):
    return len([c for c in s if c not in name_charset])<=0
    
def is_special_param(c):
    return len(c)==1 and c in ('@','*','#','?','-','$','!','0')
    
def utility_not_implemented(name, *args, **kwargs):
    raise NotImplementedError('%s utility is not implemented' % name)
    

class Utility:
    """Define utilities properties:
    func -- utility callable. See builtin module for utility samples.
    is_special -- see XCU 2.8.
    """
    def __init__(self, func, is_special=0):
        self.func = func
        self.is_special = bool(is_special)


def encodeargs(args):
    def encodearg(s):
        lines = base64.encodestring(s)
        lines = [l.splitlines()[0] for l in lines]
        return ''.join(lines)

    s = pickle.dumps(args)
    return encodearg(s)

def decodeargs(s):
    s = base64.decodestring(s)
    return pickle.loads(s)
    

class GlobError(Exception):
    pass

class Options:
    def __init__(self):
        # True if Mercurial operates with binary streams
        self.hgbinary = True

class Interpreter:
    # Implementation is very basic: the execute() method just makes a DFS on the
    # AST and execute nodes one by one. Nodes are tuple (name,obj) where name
    # is a string identifier and obj the AST element returned by the parser.
    #
    # Handler are named after the node identifiers.
    # TODO: check node names and remove the switch in execute with some
    # dynamic getattr() call to find node handlers.
    """Shell interpreter.
    
    The following debugging flags can be passed:
    debug-parsing - enable PLY debugging.
    debug-tree - print the generated AST.
    debug-cmd - trace command execution before word expansion, plus exit status.
    debug-utility - trace utility execution.
    """
    
    # List supported commands.
    COMMANDS = {
        'cat':       Utility(builtin.utility_cat,),
        'cd':       Utility(builtin.utility_cd,),
        ':':        Utility(builtin.utility_colon,),
        'echo':     Utility(builtin.utility_echo),
        'env':      Utility(builtin.utility_env),
        'exit':     Utility(builtin.utility_exit),
        'export':   Utility(builtin.builtin_export,     is_special=1),
        'egrep':    Utility(builtin.utility_egrep),
        'fgrep':    Utility(builtin.utility_fgrep),
        'gunzip':   Utility(builtin.utility_gunzip),
        'kill':     Utility(builtin.utility_kill),
        'mkdir':    Utility(builtin.utility_mkdir),
        'netstat':  Utility(builtin.utility_netstat),
        'printf':   Utility(builtin.utility_printf),
        'pwd':      Utility(builtin.utility_pwd),
        'return':   Utility(builtin.builtin_return,     is_special=1),
        'sed':      Utility(builtin.utility_sed,),
        'set':      Utility(builtin.builtin_set,),
        'shift':    Utility(builtin.builtin_shift,),
        'sleep':    Utility(builtin.utility_sleep,),
        'sort':     Utility(builtin.utility_sort,),
        'trap':     Utility(builtin.builtin_trap,       is_special=1),
        'true':     Utility(builtin.utility_true),
        'unset':    Utility(builtin.builtin_unset,      is_special=1),
        'wait':     Utility(builtin.builtin_wait,       is_special=1),
    }
    
    def __init__(self, pwd, debugflags = [], env=None, redirs=None, stdin=None,
                 stdout=None, stderr=None, opts=Options()):
        self._env = env
        if self._env is None:
            self._env = Environment(pwd)
        self._children = {}
            
        self._redirs = redirs
        self._close_redirs = False
        
        if self._redirs is None:
            if stdin is None:
                stdin = sys.stdin
            if stdout is None:
                stdout = sys.stdout
            if stderr is None:
                stderr = sys.stderr
            stdin = FileWrapper('r', stdin, False)
            stdout = FileWrapper('w', stdout, False)
            stderr = FileWrapper('w', stderr, False)
            self._redirs = Redirections(stdin, stdout, stderr)
            self._close_redirs = True
            
        self._debugflags = list(debugflags)
        self._logfile = sys.stderr
        self._options = opts
        
    def close(self):
        """Must be called when the interpreter is no longer used."""
        script = self._env.traps.get('EXIT')
        if script:
            try:
                self.execute_script(script=script)
            except:
                pass

        if self._redirs is not None and self._close_redirs:
            self._redirs.close()
            self._redirs = None
            
    def log(self, s):
        self._logfile.write(s)
        self._logfile.flush()
            
    def __getitem__(self, key):
        return self._env[key]
        
    def __setitem__(self, key, value):
        self._env[key] = value

    def options(self):
        return self._options

    def redirect(self, redirs, ios):
        def add_redir(io):
            if isinstance(io, pyshyacc.IORedirect):
                redirs.add(self, io.op, io.filename, io.io_number)
            else:
                redirs.add_here_document(self, io.name, io.content, io.io_number)
                    
        map(add_redir, ios)
        return redirs
            
    def execute_script(self, script=None, ast=None, sourced=False,
                       scriptpath=None):
        """If script is not None, parse the input. Otherwise takes the supplied
        AST. Then execute the AST.
        Return the script exit status.
        """
        try:
            if scriptpath is not None:
                self._env['0'] = os.path.abspath(scriptpath)

            if script is not None:
                debug_parsing = ('debug-parsing' in self._debugflags)    
                cmds, script = pyshyacc.parse(script, True, debug_parsing)
                if 'debug-tree' in self._debugflags:
                    pyshyacc.print_commands(cmds, self._logfile)
                    self._logfile.flush()
            else:
                cmds, script = ast, ''                
                
            status = 0
            for cmd in cmds:
                try:
                    status = self.execute(cmd)
                except ExitSignal as e:
                    if sourced:
                        raise
                    status = int(e.args[0])
                    return status
                except ShellError:
                    self._env['?'] = 1
                    raise
                if 'debug-utility' in self._debugflags or 'debug-cmd' in self._debugflags:
                    self.log('returncode ' + str(status)+ '\n')
            return status
        except CommandNotFound as e:
            print >>self._redirs.stderr, str(e)
            self._redirs.stderr.flush()
            # Command not found by non-interactive shell
            # return 127
            raise
        except RedirectionError as e:
            # TODO: should be handled depending on the utility status
            print >>self._redirs.stderr, str(e)
            self._redirs.stderr.flush()
            # Command not found by non-interactive shell
            # return 127
            raise

    def dotcommand(self, env, args):
        if len(args) < 1:
            raise ShellError('. expects at least one argument')
        path = args[0]
        if '/' not in path:
            found = env.find_in_path(args[0], True)
            if found:
                path = found[0]
        script = file(path).read()
        return self.execute_script(script=script, sourced=True)

    def execute(self, token, redirs=None):
        """Execute and AST subtree with supplied redirections overriding default
        interpreter ones.
        Return the exit status.
        """
        if not token:
            return 0
            
        if redirs is None:
            redirs = self._redirs
            
        if isinstance(token, list):
            # Commands sequence
            res = 0
            for t in token:
                res = self.execute(t, redirs)
            return res

        type, value = token
        status = 0
        if type=='simple_command':
            redirs_copy = redirs.clone()
            try:
                # TODO: define and handle command return values
                # TODO: implement set -e
                status = self._execute_simple_command(value, redirs_copy)
            finally:
                redirs_copy.close()
        elif type=='pipeline':
            status = self._execute_pipeline(value, redirs)
        elif type=='and_or':
            status = self._execute_and_or(value, redirs)
        elif type=='for_clause':
            status = self._execute_for_clause(value, redirs)
        elif type=='while_clause':
            status = self._execute_while_clause(value, redirs)
        elif type=='function_definition':
            status = self._execute_function_definition(value, redirs)
        elif type=='brace_group':
            status = self._execute_brace_group(value, redirs)
        elif type=='if_clause':
            status = self._execute_if_clause(value, redirs)
        elif type=='subshell':
            status = self.subshell(ast=value.cmds, redirs=redirs)
        elif type=='async':
            status = self._asynclist(value)
        elif type=='redirect_list':
            redirs_copy = self.redirect(redirs.clone(), value.redirs)
            try:
                status = self.execute(value.cmd, redirs_copy)
            finally:
                redirs_copy.close()
        else:
            raise NotImplementedError('Unsupported token type ' + type)

        if status < 0:
            status = 255
        return status
            
    def _execute_if_clause(self, if_clause, redirs):
        cond_status = self.execute(if_clause.cond, redirs)
        if cond_status==0:
            return self.execute(if_clause.if_cmds, redirs)
        else:
            return self.execute(if_clause.else_cmds, redirs)
      
    def _execute_brace_group(self, group, redirs):
        status = 0
        for cmd in group.cmds:
            status = self.execute(cmd, redirs)
        return status
            
    def _execute_function_definition(self, fundef, redirs):
        self._env.define_function(fundef.name, fundef.body)
        return 0
            
    def _execute_while_clause(self, while_clause, redirs):
        status = 0
        while 1:
            cond_status = 0
            for cond in while_clause.condition:
                cond_status = self.execute(cond, redirs)
                
            if cond_status:
                break
                
            for cmd in while_clause.cmds:
                status = self.execute(cmd, redirs)
                
        return status
            
    def _execute_for_clause(self, for_clause, redirs):
        if not is_name(for_clause.name):
            raise ShellSyntaxError('%s is not a valid name' % repr(for_clause.name))
        items = mappend(self.expand_token, for_clause.items)
        
        status = 0    
        for item in items:
            self._env[for_clause.name] = item
            for cmd in for_clause.cmds:
                status = self.execute(cmd, redirs)
        return status
            
    def _execute_and_or(self, or_and, redirs):
        res = self.execute(or_and.left, redirs)        
        if (or_and.op=='&&' and res==0) or (or_and.op!='&&' and res!=0):
            res = self.execute(or_and.right, redirs)
        return res
            
    def _execute_pipeline(self, pipeline, redirs):            
        if len(pipeline.commands)==1:
            status = self.execute(pipeline.commands[0], redirs)
        else:
            # Execute all commands one after the other
            status = 0
            inpath, outpath = None, None
            try:
                # Commands inputs and outputs cannot really be plugged as done
                # by a real shell. Run commands sequentially and chain their
                # input/output throught temporary files.
                tmpfd, inpath = tempfile.mkstemp()
                os.close(tmpfd)
                tmpfd, outpath = tempfile.mkstemp()
                os.close(tmpfd)
                
                inpath = win32_to_unix_path(inpath)
                outpath = win32_to_unix_path(outpath)
                
                for i, cmd in enumerate(pipeline.commands):
                    call_redirs = redirs.clone()
                    try:
                        if i!=0:
                            call_redirs.add(self, '<', inpath)
                        if i!=len(pipeline.commands)-1:
                            call_redirs.add(self, '>', outpath)
                        
                        status = self.execute(cmd, call_redirs)
                        
                        # Chain inputs/outputs
                        inpath, outpath = outpath, inpath
                    finally:
                        call_redirs.close()            
            finally:
                if inpath: os.remove(inpath)
                if outpath: os.remove(outpath)
        
        if pipeline.reverse_status:
            status = int(not status)
        self._env['?'] = status
        return status
        
    def _execute_function(self, name, args, interp, env, stdin, stdout, stderr, *others):
        assert interp is self
        
        func = env.get_function(name)
        #Set positional parameters
        prevargs = None
        try:
            prevargs = env.set_positional_args(args)
            try:
                redirs = Redirections(stdin.dup(), stdout.dup(), stderr.dup())
                try:
                    status = self.execute(func, redirs)
                finally:
                    redirs.close()
            except ReturnSignal as e:
                status = int(e.args[0])
                env['?'] = status
            return status
        finally:
            #Reset positional parameters
            if prevargs is not None:
                env.set_positional_args(prevargs)
                
    def _execute_simple_command(self, token, redirs):
        """Can raise ReturnSignal when return builtin is called, ExitSignal when
        exit is called, and other shell exceptions upon builtin failures.
        """
        debug_command = 'debug-cmd' in self._debugflags
        if debug_command:
            self.log('word' + repr(token.words) + '\n')
            self.log('assigns' + repr(token.assigns) + '\n')
            self.log('redirs' + repr(token.redirs) + '\n')
        
        is_special = None
        env = self._env
        
        try:
            # Word expansion
            args = []
            for word in token.words:                
                args += self.expand_token(word)
                if is_special is None and args:
                    is_special = env.is_function(args[0]) or \
                        (args[0] in self.COMMANDS and self.COMMANDS[args[0]].is_special)
                        
            if debug_command:
                self.log('_execute_simple_command' + str(args) + '\n')
                
            if not args:
                # Redirections happen is a subshell
                redirs = redirs.clone()
            elif not is_special:
                env = self._env.clone()
            
            # Redirections
            self.redirect(redirs, token.redirs)
                
            # Variables assignments
            res = 0
            for type,(k,v) in token.assigns:
                status, expanded = self.expand_variable((k,v))
                if status is not None:
                    res = status
                if args:
                    env.export(k, expanded)
                else:
                    env[k] = expanded
                
            if args and args[0] in ('.', 'source'):
                res = self.dotcommand(env, args[1:])
            elif args:
                if args[0] in self.COMMANDS:
                    command = self.COMMANDS[args[0]]
                elif env.is_function(args[0]):
                    command = Utility(self._execute_function, is_special=True)
                else:
                    if not '/' in args[0].replace('\\', '/'):
                        cmd = env.find_in_path(args[0])
                        if not cmd:
                            # TODO: test error code on unknown command => 127
                            raise CommandNotFound('Unknown command: "%s"' % args[0])
                    else:
                        # Handle commands like '/cygdrive/c/foo.bat'
                        cmd = cygwin_to_windows_path(args[0])
                        if not os.path.exists(cmd):
                            raise CommandNotFound('%s: No such file or directory' % args[0])
                        shebang = resolve_shebang(cmd)
                        if shebang:
                            cmd = shebang
                        else:
                            cmd = [cmd]
                    args[0:1] = cmd
                    command = Utility(builtin.run_command)
                
                # Command execution
                if 'debug-cmd' in self._debugflags:
                    self.log('redirections ' + str(redirs) + '\n')
                    
                res = command.func(args[0], args[1:], self, env,
                                   redirs.stdin(), redirs.stdout(), 
                                   redirs.stderr(), self._debugflags)
            
            if self._env.has_opt('-x'):
                # Trace command execution in shell environment
                # BUG: would be hard to reproduce a real shell behaviour since
                # the AST is not annotated with source lines/tokens.
                self._redirs.stdout().write(' '.join(args))
                
        except ReturnSignal:
            raise
        except ShellError as e:
            if is_special or isinstance(e, (ExitSignal,
                                            ShellSyntaxError, ExpansionError)):
                raise e
            self._redirs.stderr().write(str(e)+'\n')
            return 1

        return res

    def expand_token(self, word):
        """Expand a word as specified in [2.6 Word Expansions]. Return the list
        of expanded words.
        """
        status, wtrees = self._expand_word(word)
        return map(pyshlex.wordtree_as_string, wtrees)
        
    def expand_variable(self, word):
        """Return a status code (or None if no command expansion occurred)
        and a single word.
        """
        status, wtrees = self._expand_word(word, pathname=False, split=False)
        words = map(pyshlex.wordtree_as_string, wtrees)
        assert len(words)==1
        return status, words[0]
        
    def expand_here_document(self, word):
        """Return the expanded document as a single word. The here document is 
        assumed to be unquoted.
        """
        status, wtrees = self._expand_word(word, pathname=False,
                                           split=False, here_document=True)
        words = map(pyshlex.wordtree_as_string, wtrees)
        assert len(words)==1
        return words[0]
        
    def expand_redirection(self, word):
        """Return a single word."""
        return self.expand_variable(word)[1]
        
    def get_env(self):
        return self._env
        
    def _expand_word(self, token, pathname=True, split=True, here_document=False):
        wtree = pyshlex.make_wordtree(token[1], here_document=here_document)
        
        # TODO: implement tilde expansion
        def expand(wtree):
            """Return a pseudo wordtree: the tree or its subelements can be empty
            lists when no value result from the expansion.
            """
            status = None
            for part in wtree:
                if not isinstance(part, list):
                    continue
                if part[0]in ("'", '\\'):
                    continue
                elif part[0] in ('`', '$('):
                    status, result = self._expand_command(part)
                    part[:] = result
                elif part[0] in ('$', '${'):
                    part[:] = self._expand_parameter(part, wtree[0]=='"', split)
                elif part[0] in ('', '"'):
                    status, result = expand(part)
                    part[:] = result
                else:
                    raise NotImplementedError('%s expansion is not implemented'
                                              % part[0])
            # [] is returned when an expansion result in no-field,
            # like an empty $@
            wtree = [p for p in wtree if p != []]
            if len(wtree) < 3:
                return status, []
            return status, wtree
        
        status, wtree = expand(wtree)
        if len(wtree) == 0:
            return status, wtree
        wtree = pyshlex.normalize_wordtree(wtree)
        
        if split:
            wtrees = self._split_fields(wtree)
        else:
            wtrees = [wtree]
        
        if pathname:
            wtrees = mappend(self._expand_pathname, wtrees)
        
        wtrees = map(self._remove_quotes, wtrees)
        return status, wtrees
        
    def _expand_command(self, wtree):
        # BUG: there is something to do with backslashes and quoted
        # characters here
        command = pyshlex.wordtree_as_string(wtree[1:-1])
        status, output = self.subshell_output(command)
        return status, ['', output, '']
        
    def _expand_parameter(self, wtree, quoted=False, split=False):
        """Return a valid wtree or an empty list when no parameter results."""
        # Get the parameter name
        # TODO: implement weird expansion rules with ':'
        name = pyshlex.wordtree_as_string(wtree[1:-1])
        if not is_name(name) and not is_special_param(name):
            raise ExpansionError('Bad substitution "%s"' % name)
        # TODO: implement special parameters
        if name in ('@', '*'):
            args = self._env.get_positional_args()
            if len(args) == 0:
                return []
            if len(args)<2:
                return ['', ''.join(args), '']
                
            sep = self._env.get('IFS', '')[:1]
            if split and quoted and name=='@':
                # Introduce a new token to tell the caller that these parameters
                # cause a split as specified in 2.5.2
                return ['@'] + args + ['']
            else:
                return ['', sep.join(args), '']                
        
        return ['', self._env.get(name, ''), '']
        
    def _split_fields(self, wtree):
        def is_empty(split):
            return split==['', '', '']
            
        def split_positional(quoted):
            # Return a list of wtree split according positional parameters rules.
            # All remaining '@' groups are removed.
            assert quoted[0]=='"'
            
            splits = [[]]
            for part in quoted:
                if not isinstance(part, list) or part[0]!='@':
                    splits[-1].append(part)
                else:
                    # Empty or single argument list were dealt with already
                    assert len(part)>3
                    # First argument must join with the beginning part of the original word
                    splits[-1].append(part[1])
                    # Create double-quotes expressions for every argument after the first
                    for arg in part[2:-1]:
                        splits[-1].append('"')
                        splits.append(['"', arg])
            return splits
        
        # At this point, all expansions but pathnames have occured. Only quoted
        # and positional sequences remain. Thus, all candidates for field splitting 
        # are in the tree root, or are positional splits ('@') and lie in root
        # children.
        if not wtree or wtree[0] not in ('', '"'):
            # The whole token is quoted or empty, nothing to split
            return [wtree]
            
        if wtree[0]=='"':
            wtree = ['', wtree, '']
        
        result = [['', '']]
        for part in wtree[1:-1]:
            if isinstance(part, list):
                if part[0]=='"':
                    splits = split_positional(part)
                    if len(splits)<=1:
                        result[-1] += [part, '']
                    else:
                        # Terminate the current split
                        result[-1] += [splits[0], '']
                        result += splits[1:-1]
                        # Create a new split
                        result += [['', splits[-1], '']]
                else:
                    result[-1] += [part, '']
            else:
                splits = self._env.split_fields(part)
                if len(splits)<=1:
                    # No split
                    result[-1][-1] += part
                else:
                    # Terminate the current resulting part and create a new one
                    result[-1][-1] += splits[0]
                    result[-1].append('')
                    result += [['', r, ''] for r in splits[1:-1]]
                    result += [['', splits[-1]]]
        result[-1].append('')
        
        # Leading and trailing empty groups come from leading/trailing blanks
        if result and is_empty(result[-1]):
            result[-1:] = []
        if result and is_empty(result[0]):
            result[:1] = []
        return result
        
    def _expand_pathname(self, wtree):
        """See [2.6.6 Pathname Expansion]."""
        if self._env.has_opt('-f'):
            return [wtree]
        
        # All expansions have been performed, only quoted sequences should remain
        # in the tree. Generate the pattern by folding the tree, escaping special
        # characters when appear quoted
        special_chars = '*?[]'
        
        def make_pattern(wtree):
            subpattern = []
            for part in wtree[1:-1]:
                if isinstance(part, list):
                    part = make_pattern(part)
                elif wtree[0]!='':
                    for c in part:
                        # Meta-characters cannot be quoted
                        if c in special_chars:
                            raise GlobError()
                subpattern.append(part)
            return ''.join(subpattern)
            
        def pwd_glob(pattern):
            cwd = os.getcwd()
            os.chdir(self._env['PWD'])
            try:
                return glob.glob(pattern) 
            finally:
                os.chdir(cwd)    
            
        #TODO: check working directory issues here wrt relative patterns
        try:
            pattern = make_pattern(wtree)
            paths = pwd_glob(pattern)
        except GlobError:
            # BUG: Meta-characters were found in quoted sequences. The should 
            # have been used literally but this is unsupported in current glob module.
            # Instead we consider the whole tree must be used literally and
            # therefore there is no point in globbing. This is wrong when meta
            # characters are mixed with quoted meta in the same pattern like:
            # < foo*"py*" >
            paths = []
            
        if not paths:
            return [wtree]
        return [['', path, ''] for path in paths]
        
    def _remove_quotes(self, wtree):
        """See [2.6.7 Quote Removal]."""
        
        def unquote(wtree):
            unquoted = []
            for part in wtree[1:-1]:
                if isinstance(part, list):
                    part = unquote(part)
                unquoted.append(part)
            return ''.join(unquoted)
            
        return ['', unquote(wtree), '']
        
    def subshell(self, script=None, ast=None, redirs=None):
        """Execute the script or AST in a subshell, with inherited redirections
        if redirs is not None.
        """
        if redirs:
            sub_redirs = redirs
        else:
            sub_redirs = redirs.clone()
        
        subshell = None    
        try:
            subshell = Interpreter(None, self._debugflags, self._env.clone(True),
                                   sub_redirs, opts=self._options)
            return subshell.execute_script(script, ast)
        finally:
            if not redirs: sub_redirs.close()
            if subshell: subshell.close()
        
    def subshell_output(self, script):
        """Execute the script in a subshell and return the captured output."""        
        # Create temporary file to capture subshell output
        tmpfd, tmppath = tempfile.mkstemp()
        try:
            tmpfile = os.fdopen(tmpfd, 'wb')
            stdout = FileWrapper('w', tmpfile)
            
            redirs = Redirections(self._redirs.stdin().dup(),
                                  stdout,
                                  self._redirs.stderr().dup())            
            try:
                status = self.subshell(script=script, redirs=redirs)
            finally:
                redirs.close()
                redirs = None
            
            # Extract subshell standard output
            tmpfile = open(tmppath, 'rb')
            try:
                output = tmpfile.read()
                return status, output.rstrip('\n')
            finally:
                tmpfile.close()
        finally:
            os.remove(tmppath)

    def _asynclist(self, cmd):
        args = (self._env.get_variables(), cmd)
        arg = encodeargs(args)
        assert len(args) < 30*1024
        cmd = ['pysh.bat', '--ast', '-c', arg]
        p = subprocess.Popen(cmd, cwd=self._env['PWD'])
        self._children[p.pid] = p
        self._env['!'] = p.pid
        return 0

    def wait(self, pids=None):
        if not pids:
            pids = self._children.keys()

        status = 127
        for pid in pids:
            if pid not in self._children:
                continue
            p = self._children.pop(pid)
            status = p.wait()

        return status

