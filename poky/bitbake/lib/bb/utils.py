"""
BitBake Utility Functions
"""

# Copyright (C) 2004 Michael Lauer
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re, fcntl, os, string, stat, shutil, time
import sys
import errno
import logging
import bb
import bb.msg
import locale
import multiprocessing
import fcntl
import importlib
import importlib.machinery
import importlib.util
import itertools
import subprocess
import glob
import fnmatch
import traceback
import errno
import signal
import collections
import copy
import ctypes
import random
import socket
import struct
import tempfile
from subprocess import getstatusoutput
from contextlib import contextmanager
from ctypes import cdll

logger = logging.getLogger("BitBake.Util")
python_extensions = importlib.machinery.all_suffixes()


def clean_context():
    return {
        "os": os,
        "bb": bb,
        "time": time,
    }

def get_context():
    return _context


def set_context(ctx):
    _context = ctx

# Context used in better_exec, eval
_context = clean_context()

class VersionStringException(Exception):
    """Exception raised when an invalid version specification is found"""

def explode_version(s):
    r = []
    alpha_regexp = re.compile(r'^([a-zA-Z]+)(.*)$')
    numeric_regexp = re.compile(r'^(\d+)(.*)$')
    while (s != ''):
        if s[0] in string.digits:
            m = numeric_regexp.match(s)
            r.append((0, int(m.group(1))))
            s = m.group(2)
            continue
        if s[0] in string.ascii_letters:
            m = alpha_regexp.match(s)
            r.append((1, m.group(1)))
            s = m.group(2)
            continue
        if s[0] == '~':
            r.append((-1, s[0]))
        else:
            r.append((2, s[0]))
        s = s[1:]
    return r

def split_version(s):
    """Split a version string into its constituent parts (PE, PV, PR)"""
    s = s.strip(" <>=")
    e = 0
    if s.count(':'):
        e = int(s.split(":")[0])
        s = s.split(":")[1]
    r = ""
    if s.count('-'):
        r = s.rsplit("-", 1)[1]
        s = s.rsplit("-", 1)[0]
    v = s
    return (e, v, r)

def vercmp_part(a, b):
    va = explode_version(a)
    vb = explode_version(b)
    while True:
        if va == []:
            (oa, ca) = (0, None)
        else:
            (oa, ca) = va.pop(0)
        if vb == []:
            (ob, cb) = (0, None)
        else:
            (ob, cb) = vb.pop(0)
        if (oa, ca) == (0, None) and (ob, cb) == (0, None):
            return 0
        if oa < ob:
            return -1
        elif oa > ob:
            return 1
        elif ca is None:
            return -1
        elif cb is None:
            return 1
        elif ca < cb:
            return -1
        elif ca > cb:
            return 1

def vercmp(ta, tb):
    (ea, va, ra) = ta
    (eb, vb, rb) = tb

    r = int(ea or 0) - int(eb or 0)
    if (r == 0):
        r = vercmp_part(va, vb)
    if (r == 0):
        r = vercmp_part(ra, rb)
    return r

def vercmp_string(a, b):
    """ Split version strings and compare them """
    ta = split_version(a)
    tb = split_version(b)
    return vercmp(ta, tb)

def vercmp_string_op(a, b, op):
    """
    Compare two versions and check if the specified comparison operator matches the result of the comparison.
    This function is fairly liberal about what operators it will accept since there are a variety of styles
    depending on the context.
    """
    res = vercmp_string(a, b)
    if op in ('=', '=='):
        return res == 0
    elif op == '<=':
        return res <= 0
    elif op == '>=':
        return res >= 0
    elif op in ('>', '>>'):
        return res > 0
    elif op in ('<', '<<'):
        return res < 0
    elif op == '!=':
        return res != 0
    else:
        raise VersionStringException('Unsupported comparison operator "%s"' % op)

def explode_deps(s):
    """
    Take an RDEPENDS style string of format:
    "DEPEND1 (optional version) DEPEND2 (optional version) ..."
    and return a list of dependencies.
    Version information is ignored.
    """
    r = []
    l = s.split()
    flag = False
    for i in l:
        if i[0] == '(':
            flag = True
            #j = []
        if not flag:
            r.append(i)
        #else:
        #    j.append(i)
        if flag and i.endswith(')'):
            flag = False
            # Ignore version
            #r[-1] += ' ' + ' '.join(j)
    return r

def explode_dep_versions2(s, *, sort=True):
    """
    Take an RDEPENDS style string of format:
    "DEPEND1 (optional version) DEPEND2 (optional version) ..."
    and return a dictionary of dependencies and versions.
    """
    r = collections.OrderedDict()
    l = s.replace(",", "").split()
    lastdep = None
    lastcmp = ""
    lastver = ""
    incmp = False
    inversion = False
    for i in l:
        if i[0] == '(':
            incmp = True
            i = i[1:].strip()
            if not i:
                continue

        if incmp:
            incmp = False
            inversion = True
            # This list is based on behavior and supported comparisons from deb, opkg and rpm.
            #
            # Even though =<, <<, ==, !=, =>, and >> may not be supported,
            # we list each possibly valid item.
            # The build system is responsible for validation of what it supports.
            if i.startswith(('<=', '=<', '<<', '==', '!=', '>=', '=>', '>>')):
                lastcmp = i[0:2]
                i = i[2:]
            elif i.startswith(('<', '>', '=')):
                lastcmp = i[0:1]
                i = i[1:]
            else:
                # This is an unsupported case!
                raise VersionStringException('Invalid version specification in "(%s" - invalid or missing operator' % i)
                lastcmp = (i or "")
                i = ""
            i.strip()
            if not i:
                continue

        if inversion:
            if i.endswith(')'):
                i = i[:-1] or ""
                inversion = False
                if lastver and i:
                    lastver += " "
            if i:
                lastver += i
                if lastdep not in r:
                    r[lastdep] = []
                r[lastdep].append(lastcmp + " " + lastver)
            continue

        #if not inversion:
        lastdep = i
        lastver = ""
        lastcmp = ""
        if not (i in r and r[i]):
            r[lastdep] = []

    if sort:
        r = collections.OrderedDict(sorted(r.items(), key=lambda x: x[0]))
    return r

def explode_dep_versions(s):
    """
    Take an RDEPENDS style string of format:
    "DEPEND1 (optional version) DEPEND2 (optional version) ..."
    skip null value and items appeared in dependency string multiple times
    and return a dictionary of dependencies and versions.
    """
    r = explode_dep_versions2(s)
    for d in r:
        if not r[d]:
            r[d] = None
            continue
        if len(r[d]) > 1:
            bb.warn("explode_dep_versions(): Item %s appeared in dependency string '%s' multiple times with different values.  explode_dep_versions cannot cope with this." % (d, s))
        r[d] = r[d][0]
    return r

def join_deps(deps, commasep=True):
    """
    Take the result from explode_dep_versions and generate a dependency string
    """
    result = []
    for dep in deps:
        if deps[dep]:
            if isinstance(deps[dep], list):
                for v in deps[dep]:
                    result.append(dep + " (" + v + ")")
            else:
                result.append(dep + " (" + deps[dep] + ")")
        else:
            result.append(dep)
    if commasep:
        return ", ".join(result)
    else:
        return " ".join(result)

def _print_trace(body, line):
    """
    Print the Environment of a Text Body
    """
    error = []
    # print the environment of the method
    min_line = max(1, line-4)
    max_line = min(line + 4, len(body))
    for i in range(min_line, max_line + 1):
        if line == i:
            error.append(' *** %.4d:%s' % (i, body[i-1].rstrip()))
        else:
            error.append('     %.4d:%s' % (i, body[i-1].rstrip()))
    return error

def better_compile(text, file, realfile, mode = "exec", lineno = 0):
    """
    A better compile method. This method
    will print the offending lines.
    """
    try:
        cache = bb.methodpool.compile_cache(text)
        if cache:
            return cache
        # We can't add to the linenumbers for compile, we can pad to the correct number of blank lines though
        text2 = "\n" * int(lineno) + text
        code = compile(text2, realfile, mode)
        bb.methodpool.compile_cache_add(text, code)
        return code
    except Exception as e:
        error = []
        # split the text into lines again
        body = text.split('\n')
        error.append("Error in compiling python function in %s, line %s:\n" % (realfile, e.lineno))
        if hasattr(e, "lineno"):
            error.append("The code lines resulting in this error were:")
            # e.lineno: line's position in reaflile
            # lineno: function name's "position -1" in realfile
            # e.lineno - lineno: line's relative position in function
            error.extend(_print_trace(body, e.lineno - lineno))
        else:
            error.append("The function causing this error was:")
            for line in body:
                error.append(line)
        error.append("%s: %s" % (e.__class__.__name__, str(e)))

        logger.error("\n".join(error))

        e = bb.BBHandledException(e)
        raise e

def _print_exception(t, value, tb, realfile, text, context):
    error = []
    try:
        exception = traceback.format_exception_only(t, value)
        error.append('Error executing a python function in %s:\n' % realfile)

        # Strip 'us' from the stack (better_exec call) unless that was where the
        # error came from
        if tb.tb_next is not None:
            tb = tb.tb_next

        textarray = text.split('\n')

        linefailed = tb.tb_lineno

        tbextract = traceback.extract_tb(tb)
        tbformat = traceback.format_list(tbextract)
        error.append("The stack trace of python calls that resulted in this exception/failure was:")
        error.append("File: '%s', lineno: %s, function: %s" % (tbextract[0][0], tbextract[0][1], tbextract[0][2]))
        error.extend(_print_trace(textarray, linefailed))

        # See if this is a function we constructed and has calls back into other functions in
        # "text". If so, try and improve the context of the error by diving down the trace
        level = 0
        nexttb = tb.tb_next
        while nexttb is not None and (level+1) < len(tbextract):
            error.append("File: '%s', lineno: %s, function: %s" % (tbextract[level+1][0], tbextract[level+1][1], tbextract[level+1][2]))
            if tbextract[level][0] == tbextract[level+1][0] and tbextract[level+1][2] == tbextract[level][0]:
                # The code was possibly in the string we compiled ourselves
                error.extend(_print_trace(textarray, tbextract[level+1][1]))
            elif tbextract[level+1][0].startswith("/"):
                # The code looks like it might be in a file, try and load it
                try:
                    with open(tbextract[level+1][0], "r") as f:
                        text = f.readlines()
                        error.extend(_print_trace(text, tbextract[level+1][1]))
                except:
                    error.append(tbformat[level+1])
            else:
                error.append(tbformat[level+1])
            nexttb = tb.tb_next
            level = level + 1

        error.append("Exception: %s" % ''.join(exception))

        # If the exception is from spawning a task, let's be helpful and display
        # the output (which hopefully includes stderr).
        if isinstance(value, subprocess.CalledProcessError) and value.output:
            error.append("Subprocess output:")
            error.append(value.output.decode("utf-8", errors="ignore"))
    finally:
        logger.error("\n".join(error))

def better_exec(code, context, text = None, realfile = "<code>", pythonexception=False):
    """
    Similiar to better_compile, better_exec will
    print the lines that are responsible for the
    error.
    """
    import bb.parse
    if not text:
        text = code
    if not hasattr(code, "co_filename"):
        code = better_compile(code, realfile, realfile)
    try:
        exec(code, get_context(), context)
    except (bb.BBHandledException, bb.parse.SkipRecipe, bb.data_smart.ExpansionError, bb.process.ExecutionError):
        # Error already shown so passthrough, no need for traceback
        raise
    except Exception as e:
        if pythonexception:
            raise
        (t, value, tb) = sys.exc_info()
        try:
            _print_exception(t, value, tb, realfile, text, context)
        except Exception as e2:
            logger.error("Exception handler error: %s" % str(e2))

        e = bb.BBHandledException(e)
        raise e

def simple_exec(code, context):
    exec(code, get_context(), context)

def better_eval(source, locals, extraglobals = None):
    ctx = get_context()
    if extraglobals:
        ctx = copy.copy(ctx)
        for g in extraglobals:
            ctx[g] = extraglobals[g]
    return eval(source, ctx, locals)

@contextmanager
def fileslocked(files, *args, **kwargs):
    """Context manager for locking and unlocking file locks."""
    locks = []
    if files:
        for lockfile in files:
            l = bb.utils.lockfile(lockfile, *args, **kwargs)
            if l is not None:
                locks.append(l)

    try:
        yield
    finally:
        locks.reverse()
        for lock in locks:
            bb.utils.unlockfile(lock)

def lockfile(name, shared=False, retry=True, block=False):
    """
    Use the specified file as a lock file, return when the lock has
    been acquired. Returns a variable to pass to unlockfile().
    Parameters:
        retry: True to re-try locking if it fails, False otherwise
        block: True to block until the lock succeeds, False otherwise
    The retry and block parameters are kind of equivalent unless you
    consider the possibility of sending a signal to the process to break
    out - at which point you want block=True rather than retry=True.
    """
    basename = os.path.basename(name)
    if len(basename) > 255:
        root, ext = os.path.splitext(basename)
        basename = root[:255 - len(ext)] + ext

    dirname = os.path.dirname(name)
    mkdirhier(dirname)

    name = os.path.join(dirname, basename)

    if not os.access(dirname, os.W_OK):
        logger.error("Unable to acquire lock '%s', directory is not writable",
                     name)
        sys.exit(1)

    op = fcntl.LOCK_EX
    if shared:
        op = fcntl.LOCK_SH
    if not retry and not block:
        op = op | fcntl.LOCK_NB

    while True:
        # If we leave the lockfiles lying around there is no problem
        # but we should clean up after ourselves. This gives potential
        # for races though. To work around this, when we acquire the lock
        # we check the file we locked was still the lock file on disk.
        # by comparing inode numbers. If they don't match or the lockfile
        # no longer exists, we start again.

        # This implementation is unfair since the last person to request the
        # lock is the most likely to win it.

        try:
            lf = open(name, 'a+')
            fileno = lf.fileno()
            fcntl.flock(fileno, op)
            statinfo = os.fstat(fileno)
            if os.path.exists(lf.name):
                statinfo2 = os.stat(lf.name)
                if statinfo.st_ino == statinfo2.st_ino:
                    return lf
            lf.close()
        except OSError as e:
            if e.errno == errno.EACCES or e.errno == errno.ENAMETOOLONG:
                logger.error("Unable to acquire lock '%s', %s",
                             e.strerror, name)
                sys.exit(1)
            try:
                lf.close()
            except Exception:
                pass
            pass
        if not retry:
            return None

def unlockfile(lf):
    """
    Unlock a file locked using lockfile()
    """
    try:
        # If we had a shared lock, we need to promote to exclusive before
        # removing the lockfile. Attempt this, ignore failures.
        fcntl.flock(lf.fileno(), fcntl.LOCK_EX|fcntl.LOCK_NB)
        os.unlink(lf.name)
    except (IOError, OSError):
        pass
    fcntl.flock(lf.fileno(), fcntl.LOCK_UN)
    lf.close()

def _hasher(method, filename):
    import mmap

    with open(filename, "rb") as f:
        try:
            with mmap.mmap(f.fileno(), 0, access=mmap.ACCESS_READ) as mm:
                for chunk in iter(lambda: mm.read(8192), b''):
                    method.update(chunk)
        except ValueError:
            # You can't mmap() an empty file so silence this exception
            pass
    return method.hexdigest()


def md5_file(filename):
    """
    Return the hex string representation of the MD5 checksum of filename.
    """
    import hashlib
    try:
        sig = hashlib.new('MD5', usedforsecurity=False)
    except TypeError:
        # Some configurations don't appear to support two arguments
        sig = hashlib.new('MD5')
    return _hasher(sig, filename)

def sha256_file(filename):
    """
    Return the hex string representation of the 256-bit SHA checksum of
    filename.
    """
    import hashlib
    return _hasher(hashlib.sha256(), filename)

def sha1_file(filename):
    """
    Return the hex string representation of the SHA1 checksum of the filename
    """
    import hashlib
    return _hasher(hashlib.sha1(), filename)

def sha384_file(filename):
    """
    Return the hex string representation of the SHA384 checksum of the filename
    """
    import hashlib
    return _hasher(hashlib.sha384(), filename)

def sha512_file(filename):
    """
    Return the hex string representation of the SHA512 checksum of the filename
    """
    import hashlib
    return _hasher(hashlib.sha512(), filename)

def preserved_envvars_exported():
    """Variables which are taken from the environment and placed in and exported
    from the metadata"""
    return [
        'BB_TASKHASH',
        'HOME',
        'LOGNAME',
        'PATH',
        'PWD',
        'SHELL',
        'USER',
        'LC_ALL',
        'BBSERVER',
    ]

def preserved_envvars():
    """Variables which are taken from the environment and placed in the metadata"""
    v = [
        'BBPATH',
        'BB_PRESERVE_ENV',
        'BB_ENV_PASSTHROUGH_ADDITIONS',
    ]
    return v + preserved_envvars_exported()

def check_system_locale():
    """Make sure the required system locale are available and configured"""
    default_locale = locale.getlocale(locale.LC_CTYPE)

    try:
        locale.setlocale(locale.LC_CTYPE, ("en_US", "UTF-8"))
    except:
        sys.exit("Please make sure locale 'en_US.UTF-8' is available on your system")
    else:
        locale.setlocale(locale.LC_CTYPE, default_locale)

    if sys.getfilesystemencoding() != "utf-8":
        sys.exit("Please use a locale setting which supports UTF-8 (such as LANG=en_US.UTF-8).\n"
                 "Python can't change the filesystem locale after loading so we need a UTF-8 when Python starts or things won't work.")

def filter_environment(good_vars):
    """
    Create a pristine environment for bitbake. This will remove variables that
    are not known and may influence the build in a negative way.
    """

    removed_vars = {}
    for key in list(os.environ):
        if key in good_vars:
            continue

        removed_vars[key] = os.environ[key]
        del os.environ[key]

    # If we spawn a python process, we need to have a UTF-8 locale, else python's file
    # access methods will use ascii. You can't change that mode once the interpreter is
    # started so we have to ensure a locale is set. Ideally we'd use C.UTF-8 but not all
    # distros support that and we need to set something.
    os.environ["LC_ALL"] = "en_US.UTF-8"

    if removed_vars:
        logger.debug("Removed the following variables from the environment: %s", ", ".join(removed_vars.keys()))

    return removed_vars

def approved_variables():
    """
    Determine and return the list of variables which are approved
    to remain in the environment.
    """
    if 'BB_PRESERVE_ENV' in os.environ:
        return os.environ.keys()
    approved = []
    if 'BB_ENV_PASSTHROUGH' in os.environ:
        approved = os.environ['BB_ENV_PASSTHROUGH'].split()
        approved.extend(['BB_ENV_PASSTHROUGH'])
    else:
        approved = preserved_envvars()
    if 'BB_ENV_PASSTHROUGH_ADDITIONS' in os.environ:
        approved.extend(os.environ['BB_ENV_PASSTHROUGH_ADDITIONS'].split())
        if 'BB_ENV_PASSTHROUGH_ADDITIONS' not in approved:
            approved.extend(['BB_ENV_PASSTHROUGH_ADDITIONS'])
    return approved

def clean_environment():
    """
    Clean up any spurious environment variables. This will remove any
    variables the user hasn't chosen to preserve.
    """
    if 'BB_PRESERVE_ENV' not in os.environ:
        good_vars = approved_variables()
        return filter_environment(good_vars)

    return {}

def empty_environment():
    """
    Remove all variables from the environment.
    """
    for s in list(os.environ.keys()):
        os.unsetenv(s)
        del os.environ[s]

def build_environment(d):
    """
    Build an environment from all exported variables.
    """
    import bb.data
    for var in bb.data.keys(d):
        export = d.getVarFlag(var, "export", False)
        if export:
            os.environ[var] = d.getVar(var) or ""

def _check_unsafe_delete_path(path):
    """
    Basic safeguard against recursively deleting something we shouldn't. If it returns True,
    the caller should raise an exception with an appropriate message.
    NOTE: This is NOT meant to be a security mechanism - just a guard against silly mistakes
    with potentially disastrous results.
    """
    extra = ''
    # HOME might not be /home/something, so in case we can get it, check against it
    homedir = os.environ.get('HOME', '')
    if homedir:
        extra = '|%s' % homedir
    if re.match('(/|//|/home|/home/[^/]*%s)$' % extra, os.path.abspath(path)):
        return True
    return False

def remove(path, recurse=False, ionice=False):
    """Equivalent to rm -f or rm -rf"""
    if not path:
        return
    if recurse:
        for name in glob.glob(path):
            if _check_unsafe_delete_path(name):
                raise Exception('bb.utils.remove: called with dangerous path "%s" and recurse=True, refusing to delete!' % name)
        # shutil.rmtree(name) would be ideal but its too slow
        cmd = []
        if ionice:
            cmd = ['ionice', '-c', '3']
        subprocess.check_call(cmd + ['rm', '-rf'] + glob.glob(path))
        return
    for name in glob.glob(path):
        try:
            os.unlink(name)
        except OSError as exc:
            if exc.errno != errno.ENOENT:
                raise

def prunedir(topdir, ionice=False):
    """ Delete everything reachable from the directory named in 'topdir'. """
    # CAUTION:  This is dangerous!
    if _check_unsafe_delete_path(topdir):
        raise Exception('bb.utils.prunedir: called with dangerous path "%s", refusing to delete!' % topdir)
    remove(topdir, recurse=True, ionice=ionice)

#
# Could also use return re.compile("(%s)" % "|".join(map(re.escape, suffixes))).sub(lambda mo: "", var)
# but thats possibly insane and suffixes is probably going to be small
#
def prune_suffix(var, suffixes, d):
    """
    See if var ends with any of the suffixes listed and
    remove it if found
    """
    for suffix in suffixes:
        if suffix and var.endswith(suffix):
            return var[:-len(suffix)]
    return var

def mkdirhier(directory):
    """Create a directory like 'mkdir -p', but does not complain if
    directory already exists like os.makedirs
    """
    if '${' in str(directory):
        bb.fatal("Directory name {} contains unexpanded bitbake variable. This may cause build failures and WORKDIR polution.".format(directory))
    try:
        os.makedirs(directory)
    except OSError as e:
        if e.errno != errno.EEXIST or not os.path.isdir(directory):
            raise e

def movefile(src, dest, newmtime = None, sstat = None):
    """Moves a file from src to dest, preserving all permissions and
    attributes; mtime will be preserved even when moving across
    filesystems.  Returns true on success and false on failure. Move is
    atomic.
    """

    #print "movefile(" + src + "," + dest + "," + str(newmtime) + "," + str(sstat) + ")"
    try:
        if not sstat:
            sstat = os.lstat(src)
    except Exception as e:
        logger.warning("movefile: Stating source file failed...", e)
        return None

    destexists = 1
    try:
        dstat = os.lstat(dest)
    except:
        dstat = os.lstat(os.path.dirname(dest))
        destexists = 0

    if destexists:
        if stat.S_ISLNK(dstat[stat.ST_MODE]):
            try:
                os.unlink(dest)
                destexists = 0
            except Exception as e:
                pass

    if stat.S_ISLNK(sstat[stat.ST_MODE]):
        try:
            target = os.readlink(src)
            if destexists and not stat.S_ISDIR(dstat[stat.ST_MODE]):
                os.unlink(dest)
            os.symlink(target, dest)
            #os.lchown(dest,sstat[stat.ST_UID],sstat[stat.ST_GID])
            os.unlink(src)
            return os.lstat(dest)
        except Exception as e:
            logger.warning("movefile: failed to properly create symlink:", dest, "->", target, e)
            return None

    renamefailed = 1
    # os.rename needs to know the dest path ending with file name
    # so append the file name to a path only if it's a dir specified
    srcfname = os.path.basename(src)
    destpath = os.path.join(dest, srcfname) if os.path.isdir(dest) \
                else dest

    if sstat[stat.ST_DEV] == dstat[stat.ST_DEV]:
        try:
            bb.utils.rename(src, destpath)
            renamefailed = 0
        except Exception as e:
            if e.errno != errno.EXDEV:
                # Some random error.
                logger.warning("movefile: Failed to move", src, "to", dest, e)
                return None
            # Invalid cross-device-link 'bind' mounted or actually Cross-Device

    if renamefailed:
        didcopy = 0
        if stat.S_ISREG(sstat[stat.ST_MODE]):
            try: # For safety copy then move it over.
                shutil.copyfile(src, destpath + "#new")
                bb.utils.rename(destpath + "#new", destpath)
                didcopy = 1
            except Exception as e:
                logger.warning('movefile: copy', src, '->', dest, 'failed.', e)
                return None
        else:
            #we don't yet handle special, so we need to fall back to /bin/mv
            a = getstatusoutput("/bin/mv -f " + "'" + src + "' '" + dest + "'")
            if a[0] != 0:
                logger.warning("movefile: Failed to move special file:" + src + "' to '" + dest + "'", a)
                return None # failure
        try:
            if didcopy:
                os.lchown(destpath, sstat[stat.ST_UID], sstat[stat.ST_GID])
                os.chmod(destpath, stat.S_IMODE(sstat[stat.ST_MODE])) # Sticky is reset on chown
                os.unlink(src)
        except Exception as e:
            logger.warning("movefile: Failed to chown/chmod/unlink", dest, e)
            return None

    if newmtime:
        os.utime(destpath, (newmtime, newmtime))
    else:
        os.utime(destpath, (sstat[stat.ST_ATIME], sstat[stat.ST_MTIME]))
        newmtime = sstat[stat.ST_MTIME]
    return newmtime

def copyfile(src, dest, newmtime = None, sstat = None):
    """
    Copies a file from src to dest, preserving all permissions and
    attributes; mtime will be preserved even when moving across
    filesystems.  Returns true on success and false on failure.
    """
    #print "copyfile(" + src + "," + dest + "," + str(newmtime) + "," + str(sstat) + ")"
    try:
        if not sstat:
            sstat = os.lstat(src)
    except Exception as e:
        logger.warning("copyfile: stat of %s failed (%s)" % (src, e))
        return False

    destexists = 1
    try:
        dstat = os.lstat(dest)
    except:
        dstat = os.lstat(os.path.dirname(dest))
        destexists = 0

    if destexists:
        if stat.S_ISLNK(dstat[stat.ST_MODE]):
            try:
                os.unlink(dest)
                destexists = 0
            except Exception as e:
                pass

    if stat.S_ISLNK(sstat[stat.ST_MODE]):
        try:
            target = os.readlink(src)
            if destexists and not stat.S_ISDIR(dstat[stat.ST_MODE]):
                os.unlink(dest)
            os.symlink(target, dest)
            os.lchown(dest,sstat[stat.ST_UID],sstat[stat.ST_GID])
            return os.lstat(dest)
        except Exception as e:
            logger.warning("copyfile: failed to create symlink %s to %s (%s)" % (dest, target, e))
            return False

    if stat.S_ISREG(sstat[stat.ST_MODE]):
        try:
            srcchown = False
            if not os.access(src, os.R_OK):
                # Make sure we can read it
                srcchown = True
                os.chmod(src, sstat[stat.ST_MODE] | stat.S_IRUSR)

            # For safety copy then move it over.
            shutil.copyfile(src, dest + "#new")
            bb.utils.rename(dest + "#new", dest)
        except Exception as e:
            logger.warning("copyfile: copy %s to %s failed (%s)" % (src, dest, e))
            return False
        finally:
            if srcchown:
                os.chmod(src, sstat[stat.ST_MODE])
                os.utime(src, (sstat[stat.ST_ATIME], sstat[stat.ST_MTIME]))

    else:
        #we don't yet handle special, so we need to fall back to /bin/mv
        a = getstatusoutput("/bin/cp -f " + "'" + src + "' '" + dest + "'")
        if a[0] != 0:
            logger.warning("copyfile: failed to copy special file %s to %s (%s)" % (src, dest, a))
            return False # failure
    try:
        os.lchown(dest, sstat[stat.ST_UID], sstat[stat.ST_GID])
        os.chmod(dest, stat.S_IMODE(sstat[stat.ST_MODE])) # Sticky is reset on chown
    except Exception as e:
        logger.warning("copyfile: failed to chown/chmod %s (%s)" % (dest, e))
        return False

    if newmtime:
        os.utime(dest, (newmtime, newmtime))
    else:
        os.utime(dest, (sstat[stat.ST_ATIME], sstat[stat.ST_MTIME]))
        newmtime = sstat[stat.ST_MTIME]
    return newmtime

def break_hardlinks(src, sstat = None):
    """
    Ensures src is the only hardlink to this file.  Other hardlinks,
    if any, are not affected (other than in their st_nlink value, of
    course).  Returns true on success and false on failure.

    """
    try:
        if not sstat:
            sstat = os.lstat(src)
    except Exception as e:
        logger.warning("break_hardlinks: stat of %s failed (%s)" % (src, e))
        return False
    if sstat[stat.ST_NLINK] == 1:
        return True
    return copyfile(src, src, sstat=sstat)

def which(path, item, direction = 0, history = False, executable=False):
    """
    Locate `item` in the list of paths `path` (colon separated string like $PATH).
    If `direction` is non-zero then the list is reversed.
    If `history` is True then the list of candidates also returned as result,history.
    If `executable` is True then the candidate has to be an executable file,
    otherwise the candidate simply has to exist.
    """

    if executable:
        is_candidate = lambda p: os.path.isfile(p) and os.access(p, os.X_OK)
    else:
        is_candidate = lambda p: os.path.exists(p)

    hist = []
    paths = (path or "").split(':')
    if direction != 0:
        paths.reverse()

    for p in paths:
        next = os.path.join(p, item)
        hist.append(next)
        if is_candidate(next):
            if not os.path.isabs(next):
                next = os.path.abspath(next)
            if history:
                return next, hist
            return next

    if history:
        return "", hist
    return ""

@contextmanager
def umask(new_mask):
    """
    Context manager to set the umask to a specific mask, and restore it afterwards.
    """
    current_mask = os.umask(new_mask)
    try:
        yield
    finally:
        os.umask(current_mask)

def to_boolean(string, default=None):
    """
    Check input string and return boolean value True/False/None
    depending upon the checks
    """
    if not string:
        return default

    if isinstance(string, int):
        return string != 0

    normalized = string.lower()
    if normalized in ("y", "yes", "1", "true"):
        return True
    elif normalized in ("n", "no", "0", "false"):
        return False
    else:
        raise ValueError("Invalid value for to_boolean: %s" % string)

def contains(variable, checkvalues, truevalue, falsevalue, d):
    """Check if a variable contains all the values specified.

    Arguments:

    variable -- the variable name. This will be fetched and expanded (using
    d.getVar(variable)) and then split into a set().

    checkvalues -- if this is a string it is split on whitespace into a set(),
    otherwise coerced directly into a set().

    truevalue -- the value to return if checkvalues is a subset of variable.

    falsevalue -- the value to return if variable is empty or if checkvalues is
    not a subset of variable.

    d -- the data store.
    """

    val = d.getVar(variable)
    if not val:
        return falsevalue
    val = set(val.split())
    if isinstance(checkvalues, str):
        checkvalues = set(checkvalues.split())
    else:
        checkvalues = set(checkvalues)
    if checkvalues.issubset(val):
        return truevalue
    return falsevalue

def contains_any(variable, checkvalues, truevalue, falsevalue, d):
    """Check if a variable contains any values specified.

    Arguments:

    variable -- the variable name. This will be fetched and expanded (using
    d.getVar(variable)) and then split into a set().

    checkvalues -- if this is a string it is split on whitespace into a set(),
    otherwise coerced directly into a set().

    truevalue -- the value to return if checkvalues is a subset of variable.

    falsevalue -- the value to return if variable is empty or if checkvalues is
    not a subset of variable.

    d -- the data store.
    """
    val = d.getVar(variable)
    if not val:
        return falsevalue
    val = set(val.split())
    if isinstance(checkvalues, str):
        checkvalues = set(checkvalues.split())
    else:
        checkvalues = set(checkvalues)
    if checkvalues & val:
        return truevalue
    return falsevalue

def filter(variable, checkvalues, d):
    """Return all words in the variable that are present in the checkvalues.

    Arguments:

    variable -- the variable name. This will be fetched and expanded (using
    d.getVar(variable)) and then split into a set().

    checkvalues -- if this is a string it is split on whitespace into a set(),
    otherwise coerced directly into a set().

    d -- the data store.
    """

    val = d.getVar(variable)
    if not val:
        return ''
    val = set(val.split())
    if isinstance(checkvalues, str):
        checkvalues = set(checkvalues.split())
    else:
        checkvalues = set(checkvalues)
    return ' '.join(sorted(checkvalues & val))


def get_referenced_vars(start_expr, d):
    """
    :return: names of vars referenced in start_expr (recursively), in quasi-BFS order (variables within the same level
    are ordered arbitrarily)
    """

    seen = set()
    ret = []

    # The first entry in the queue is the unexpanded start expression
    queue = collections.deque([start_expr])
    # Subsequent entries will be variable names, so we need to track whether or not entry requires getVar
    is_first = True

    empty_data = bb.data.init()
    while queue:
        entry = queue.popleft()
        if is_first:
            # Entry is the start expression - no expansion needed
            is_first = False
            expression = entry
        else:
            # This is a variable name - need to get the value
            expression = d.getVar(entry, False)
            ret.append(entry)

        # expandWithRefs is how we actually get the referenced variables in the expression. We call it using an empty
        # data store because we only want the variables directly used in the expression. It returns a set, which is what
        # dooms us to only ever be "quasi-BFS" rather than full BFS.
        new_vars = empty_data.expandWithRefs(expression, None).references - set(seen)

        queue.extend(new_vars)
        seen.update(new_vars)
    return ret


def cpu_count():
    try:
        return len(os.sched_getaffinity(0))
    except OSError:
        return multiprocessing.cpu_count()

def nonblockingfd(fd):
    fcntl.fcntl(fd, fcntl.F_SETFL, fcntl.fcntl(fd, fcntl.F_GETFL) | os.O_NONBLOCK)

def process_profilelog(fn, pout = None):
    # Either call with a list of filenames and set pout or a filename and optionally pout.
    if not pout:
        pout = fn + '.processed'

    with open(pout, 'w') as pout:
        import pstats
        if isinstance(fn, list):
            p = pstats.Stats(*fn, stream=pout)
        else:
            p = pstats.Stats(fn, stream=pout)
        p.sort_stats('time')
        p.print_stats()
        p.print_callers()
        p.sort_stats('cumulative')
        p.print_stats()

        pout.flush()

#
# Was present to work around multiprocessing pool bugs in python < 2.7.3
#
def multiprocessingpool(*args, **kwargs):

    import multiprocessing.pool
    #import multiprocessing.util
    #multiprocessing.util.log_to_stderr(10)
    # Deal with a multiprocessing bug where signals to the processes would be delayed until the work
    # completes. Putting in a timeout means the signals (like SIGINT/SIGTERM) get processed.
    def wrapper(func):
        def wrap(self, timeout=None):
            return func(self, timeout=timeout if timeout is not None else 1e100)
        return wrap
    multiprocessing.pool.IMapIterator.next = wrapper(multiprocessing.pool.IMapIterator.next)

    return multiprocessing.Pool(*args, **kwargs)

def exec_flat_python_func(func, *args, **kwargs):
    """Execute a flat python function (defined with def funcname(args):...)"""
    # Prepare a small piece of python code which calls the requested function
    # To do this we need to prepare two things - a set of variables we can use to pass
    # the values of arguments into the calling function, and the list of arguments for
    # the function being called
    context = {}
    funcargs = []
    # Handle unnamed arguments
    aidx = 1
    for arg in args:
        argname = 'arg_%s' % aidx
        context[argname] = arg
        funcargs.append(argname)
        aidx += 1
    # Handle keyword arguments
    context.update(kwargs)
    funcargs.extend(['%s=%s' % (arg, arg) for arg in kwargs.keys()])
    code = 'retval = %s(%s)' % (func, ', '.join(funcargs))
    comp = bb.utils.better_compile(code, '<string>', '<string>')
    bb.utils.better_exec(comp, context, code, '<string>')
    return context['retval']

def edit_metadata(meta_lines, variables, varfunc, match_overrides=False):
    """Edit lines from a recipe or config file and modify one or more
    specified variable values set in the file using a specified callback
    function. Lines are expected to have trailing newlines.
    Parameters:
        meta_lines: lines from the file; can be a list or an iterable
            (e.g. file pointer)
        variables: a list of variable names to look for. Functions
            may also be specified, but must be specified with '()' at
            the end of the name. Note that the function doesn't have
            any intrinsic understanding of :append, :prepend, :remove,
            or overrides, so these are considered as part of the name.
            These values go into a regular expression, so regular
            expression syntax is allowed.
        varfunc: callback function called for every variable matching
            one of the entries in the variables parameter. The function
            should take four arguments:
                varname: name of variable matched
                origvalue: current value in file
                op: the operator (e.g. '+=')
                newlines: list of lines up to this point. You can use
                    this to prepend lines before this variable setting
                    if you wish.
            and should return a four-element tuple:
                newvalue: new value to substitute in, or None to drop
                    the variable setting entirely. (If the removal
                    results in two consecutive blank lines, one of the
                    blank lines will also be dropped).
                newop: the operator to use - if you specify None here,
                    the original operation will be used.
                indent: number of spaces to indent multi-line entries,
                    or -1 to indent up to the level of the assignment
                    and opening quote, or a string to use as the indent.
                minbreak: True to allow the first element of a
                    multi-line value to continue on the same line as
                    the assignment, False to indent before the first
                    element.
            To clarify, if you wish not to change the value, then you
            would return like this: return origvalue, None, 0, True
        match_overrides: True to match items with _overrides on the end,
            False otherwise
    Returns a tuple:
        updated:
            True if changes were made, False otherwise.
        newlines:
            Lines after processing
    """

    var_res = {}
    if match_overrides:
        override_re = r'(_[a-zA-Z0-9-_$(){}]+)?'
    else:
        override_re = ''
    for var in variables:
        if var.endswith('()'):
            var_res[var] = re.compile(r'^(%s%s)[ \\t]*\([ \\t]*\)[ \\t]*{' % (var[:-2].rstrip(), override_re))
        else:
            var_res[var] = re.compile(r'^(%s%s)[ \\t]*[?+:.]*=[+.]*[ \\t]*(["\'])' % (var, override_re))

    updated = False
    varset_start = ''
    varlines = []
    newlines = []
    in_var = None
    full_value = ''
    var_end = ''

    def handle_var_end():
        prerun_newlines = newlines[:]
        op = varset_start[len(in_var):].strip()
        (newvalue, newop, indent, minbreak) = varfunc(in_var, full_value, op, newlines)
        changed = (prerun_newlines != newlines)

        if newvalue is None:
            # Drop the value
            return True
        elif newvalue != full_value or (newop not in [None, op]):
            if newop not in [None, op]:
                # Callback changed the operator
                varset_new = "%s %s" % (in_var, newop)
            else:
                varset_new = varset_start

            if isinstance(indent, int):
                if indent == -1:
                    indentspc = ' ' * (len(varset_new) + 2)
                else:
                    indentspc = ' ' * indent
            else:
                indentspc = indent
            if in_var.endswith('()'):
                # A function definition
                if isinstance(newvalue, list):
                    newlines.append('%s {\n%s%s\n}\n' % (varset_new, indentspc, ('\n%s' % indentspc).join(newvalue)))
                else:
                    if not newvalue.startswith('\n'):
                        newvalue = '\n' + newvalue
                    if not newvalue.endswith('\n'):
                        newvalue = newvalue + '\n'
                    newlines.append('%s {%s}\n' % (varset_new, newvalue))
            else:
                # Normal variable
                if isinstance(newvalue, list):
                    if not newvalue:
                        # Empty list -> empty string
                        newlines.append('%s ""\n' % varset_new)
                    elif minbreak:
                        # First item on first line
                        if len(newvalue) == 1:
                            newlines.append('%s "%s"\n' % (varset_new, newvalue[0]))
                        else:
                            newlines.append('%s "%s \\\n' % (varset_new, newvalue[0]))
                            for item in newvalue[1:]:
                                newlines.append('%s%s \\\n' % (indentspc, item))
                            newlines.append('%s"\n' % indentspc)
                    else:
                        # No item on first line
                        newlines.append('%s " \\\n' % varset_new)
                        for item in newvalue:
                            newlines.append('%s%s \\\n' % (indentspc, item))
                        newlines.append('%s"\n' % indentspc)
                else:
                    newlines.append('%s "%s"\n' % (varset_new, newvalue))
            return True
        else:
            # Put the old lines back where they were
            newlines.extend(varlines)
            # If newlines was touched by the function, we'll need to return True
            return changed

    checkspc = False

    for line in meta_lines:
        if in_var:
            value = line.rstrip()
            varlines.append(line)
            if in_var.endswith('()'):
                full_value += '\n' + value
            else:
                full_value += value[:-1]
            if value.endswith(var_end):
                if in_var.endswith('()'):
                    if full_value.count('{') - full_value.count('}') >= 0:
                        continue
                    full_value = full_value[:-1]
                if handle_var_end():
                    updated = True
                    checkspc = True
                in_var = None
        else:
            skip = False
            for (varname, var_re) in var_res.items():
                res = var_re.match(line)
                if res:
                    isfunc = varname.endswith('()')
                    if isfunc:
                        splitvalue = line.split('{', 1)
                        var_end = '}'
                    else:
                        var_end = res.groups()[-1]
                        splitvalue = line.split(var_end, 1)
                    varset_start = splitvalue[0].rstrip()
                    value = splitvalue[1].rstrip()
                    if not isfunc and value.endswith('\\'):
                        value = value[:-1]
                    full_value = value
                    varlines = [line]
                    in_var = res.group(1)
                    if isfunc:
                        in_var += '()'
                    if value.endswith(var_end):
                        full_value = full_value[:-1]
                        if handle_var_end():
                            updated = True
                            checkspc = True
                        in_var = None
                    skip = True
                    break
            if not skip:
                if checkspc:
                    checkspc = False
                    if newlines and newlines[-1] == '\n' and line == '\n':
                        # Squash blank line if there are two consecutive blanks after a removal
                        continue
                newlines.append(line)
    return (updated, newlines)


def edit_metadata_file(meta_file, variables, varfunc):
    """Edit a recipe or config file and modify one or more specified
    variable values set in the file using a specified callback function.
    The file is only written to if the value(s) actually change.
    This is basically the file version of edit_metadata(), see that
    function's description for parameter/usage information.
    Returns True if the file was written to, False otherwise.
    """
    with open(meta_file, 'r') as f:
        (updated, newlines) = edit_metadata(f, variables, varfunc)
    if updated:
        with open(meta_file, 'w') as f:
            f.writelines(newlines)
    return updated


def edit_bblayers_conf(bblayers_conf, add, remove, edit_cb=None):
    """Edit bblayers.conf, adding and/or removing layers
    Parameters:
        bblayers_conf: path to bblayers.conf file to edit
        add: layer path (or list of layer paths) to add; None or empty
            list to add nothing
        remove: layer path (or list of layer paths) to remove; None or
            empty list to remove nothing
        edit_cb: optional callback function that will be called after
            processing adds/removes once per existing entry.
    Returns a tuple:
        notadded: list of layers specified to be added but weren't
            (because they were already in the list)
        notremoved: list of layers that were specified to be removed
            but weren't (because they weren't in the list)
    """

    import fnmatch

    def remove_trailing_sep(pth):
        if pth and pth[-1] == os.sep:
            pth = pth[:-1]
        return pth

    approved = bb.utils.approved_variables()
    def canonicalise_path(pth):
        pth = remove_trailing_sep(pth)
        if 'HOME' in approved and '~' in pth:
            pth = os.path.expanduser(pth)
        return pth

    def layerlist_param(value):
        if not value:
            return []
        elif isinstance(value, list):
            return [remove_trailing_sep(x) for x in value]
        else:
            return [remove_trailing_sep(value)]

    addlayers = layerlist_param(add)
    removelayers = layerlist_param(remove)

    # Need to use a list here because we can't set non-local variables from a callback in python 2.x
    bblayercalls = []
    removed = []
    plusequals = False
    orig_bblayers = []

    def handle_bblayers_firstpass(varname, origvalue, op, newlines):
        bblayercalls.append(op)
        if op == '=':
            del orig_bblayers[:]
        orig_bblayers.extend([canonicalise_path(x) for x in origvalue.split()])
        return (origvalue, None, 2, False)

    def handle_bblayers(varname, origvalue, op, newlines):
        updated = False
        bblayers = [remove_trailing_sep(x) for x in origvalue.split()]
        if removelayers:
            for removelayer in removelayers:
                for layer in bblayers:
                    if fnmatch.fnmatch(canonicalise_path(layer), canonicalise_path(removelayer)):
                        updated = True
                        bblayers.remove(layer)
                        removed.append(removelayer)
                        break
        if addlayers and not plusequals:
            for addlayer in addlayers:
                if addlayer not in bblayers:
                    updated = True
                    bblayers.append(addlayer)
            del addlayers[:]

        if edit_cb:
            newlist = []
            for layer in bblayers:
                res = edit_cb(layer, canonicalise_path(layer))
                if res != layer:
                    newlist.append(res)
                    updated = True
                else:
                    newlist.append(layer)
            bblayers = newlist

        if updated:
            if op == '+=' and not bblayers:
                bblayers = None
            return (bblayers, None, 2, False)
        else:
            return (origvalue, None, 2, False)

    with open(bblayers_conf, 'r') as f:
        (_, newlines) = edit_metadata(f, ['BBLAYERS'], handle_bblayers_firstpass)

    if not bblayercalls:
        raise Exception('Unable to find BBLAYERS in %s' % bblayers_conf)

    # Try to do the "smart" thing depending on how the user has laid out
    # their bblayers.conf file
    if bblayercalls.count('+=') > 1:
        plusequals = True

    removelayers_canon = [canonicalise_path(layer) for layer in removelayers]
    notadded = []
    for layer in addlayers:
        layer_canon = canonicalise_path(layer)
        if layer_canon in orig_bblayers and not layer_canon in removelayers_canon:
            notadded.append(layer)
    notadded_canon = [canonicalise_path(layer) for layer in notadded]
    addlayers[:] = [layer for layer in addlayers if canonicalise_path(layer) not in notadded_canon]

    (updated, newlines) = edit_metadata(newlines, ['BBLAYERS'], handle_bblayers)
    if addlayers:
        # Still need to add these
        for addlayer in addlayers:
            newlines.append('BBLAYERS += "%s"\n' % addlayer)
        updated = True

    if updated:
        with open(bblayers_conf, 'w') as f:
            f.writelines(newlines)

    notremoved = list(set(removelayers) - set(removed))

    return (notadded, notremoved)

def get_collection_res(d):
    collections = (d.getVar('BBFILE_COLLECTIONS') or '').split()
    collection_res = {}
    for collection in collections:
        collection_res[collection] = d.getVar('BBFILE_PATTERN_%s' % collection) or ''

    return collection_res


def get_file_layer(filename, d, collection_res={}):
    """Determine the collection (as defined by a layer's layer.conf file) containing the specified file"""
    if not collection_res:
        collection_res = get_collection_res(d)

    def path_to_layer(path):
        # Use longest path so we handle nested layers
        matchlen = 0
        match = None
        for collection, regex in collection_res.items():
            if len(regex) > matchlen and re.match(regex, path):
                matchlen = len(regex)
                match = collection
        return match

    result = None
    bbfiles = (d.getVar('BBFILES_PRIORITIZED') or '').split()
    bbfilesmatch = False
    for bbfilesentry in bbfiles:
        if fnmatch.fnmatchcase(filename, bbfilesentry):
            bbfilesmatch = True
            result = path_to_layer(bbfilesentry)
            break

    if not bbfilesmatch:
        # Probably a bbclass
        result = path_to_layer(filename)

    return result


# Constant taken from http://linux.die.net/include/linux/prctl.h
PR_SET_PDEATHSIG = 1

class PrCtlError(Exception):
    pass

def signal_on_parent_exit(signame):
    """
    Trigger signame to be sent when the parent process dies
    """
    signum = getattr(signal, signame)
    # http://linux.die.net/man/2/prctl
    result = cdll['libc.so.6'].prctl(PR_SET_PDEATHSIG, signum)
    if result != 0:
        raise PrCtlError('prctl failed with error code %s' % result)

#
# Manually call the ioprio syscall. We could depend on other libs like psutil
# however this gets us enough of what we need to bitbake for now without the
# dependency
#
_unamearch = os.uname()[4]
IOPRIO_WHO_PROCESS = 1
IOPRIO_CLASS_SHIFT = 13

def ioprio_set(who, cls, value):
    NR_ioprio_set = None
    if _unamearch == "x86_64":
      NR_ioprio_set = 251
    elif _unamearch[0] == "i" and _unamearch[2:3] == "86":
      NR_ioprio_set = 289
    elif _unamearch == "aarch64":
      NR_ioprio_set = 30

    if NR_ioprio_set:
        ioprio = value | (cls << IOPRIO_CLASS_SHIFT)
        rc = cdll['libc.so.6'].syscall(NR_ioprio_set, IOPRIO_WHO_PROCESS, who, ioprio)
        if rc != 0:
            raise ValueError("Unable to set ioprio, syscall returned %s" % rc)
    else:
        bb.warn("Unable to set IO Prio for arch %s" % _unamearch)

def set_process_name(name):
    from ctypes import cdll, byref, create_string_buffer
    # This is nice to have for debugging, not essential
    try:
        libc = cdll.LoadLibrary('libc.so.6')
        buf = create_string_buffer(bytes(name, 'utf-8'))
        libc.prctl(15, byref(buf), 0, 0, 0)
    except:
        pass

def enable_loopback_networking():
    # From bits/ioctls.h
    SIOCGIFFLAGS = 0x8913
    SIOCSIFFLAGS = 0x8914
    SIOCSIFADDR = 0x8916
    SIOCSIFNETMASK = 0x891C

    # if.h
    IFF_UP = 0x1
    IFF_RUNNING = 0x40

    # bits/socket.h
    AF_INET = 2

    # char ifr_name[IFNAMSIZ=16]
    ifr_name = struct.pack("@16s", b"lo")
    def netdev_req(fd, req, data = b""):
        # Pad and add interface name
        data = ifr_name + data + (b'\x00' * (16 - len(data)))
        # Return all data after interface name
        return fcntl.ioctl(fd, req, data)[16:]

    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_IP) as sock:
        fd = sock.fileno()

        # struct sockaddr_in ifr_addr { unsigned short family; uint16_t sin_port ; uint32_t in_addr; }
        req = struct.pack("@H", AF_INET) + struct.pack("=H4B", 0, 127, 0, 0, 1)
        netdev_req(fd, SIOCSIFADDR, req)

        # short ifr_flags
        flags = struct.unpack_from('@h', netdev_req(fd, SIOCGIFFLAGS))[0]
        flags |= IFF_UP | IFF_RUNNING
        netdev_req(fd, SIOCSIFFLAGS, struct.pack('@h', flags))

        # struct sockaddr_in ifr_netmask
        req = struct.pack("@H", AF_INET) + struct.pack("=H4B", 0, 255, 0, 0, 0)
        netdev_req(fd, SIOCSIFNETMASK, req)

def disable_network(uid=None, gid=None):
    """
    Disable networking in the current process if the kernel supports it, else
    just return after logging to debug. To do this we need to create a new user
    namespace, then map back to the original uid/gid.
    """
    libc = ctypes.CDLL('libc.so.6')

    # From sched.h
    # New user namespace
    CLONE_NEWUSER = 0x10000000
    # New network namespace
    CLONE_NEWNET = 0x40000000

    if uid is None:
        uid = os.getuid()
    if gid is None:
        gid = os.getgid()

    ret = libc.unshare(CLONE_NEWNET | CLONE_NEWUSER)
    if ret != 0:
        logger.debug("System doesn't support disabling network without admin privs")
        return
    with open("/proc/self/uid_map", "w") as f:
        f.write("%s %s 1" % (uid, uid))
    with open("/proc/self/setgroups", "w") as f:
        f.write("deny")
    with open("/proc/self/gid_map", "w") as f:
        f.write("%s %s 1" % (gid, gid))

def export_proxies(d):
    from bb.fetch2 import get_fetcher_environment
    """ export common proxies variables from datastore to environment """
    newenv = get_fetcher_environment(d)
    for v in newenv:
        os.environ[v] = newenv[v]

def load_plugins(logger, plugins, pluginpath):
    def load_plugin(name):
        logger.debug('Loading plugin %s' % name)
        spec = importlib.machinery.PathFinder.find_spec(name, path=[pluginpath] )
        if spec:
            mod = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(mod)
            return mod

    logger.debug('Loading plugins from %s...' % pluginpath)

    expanded = (glob.glob(os.path.join(pluginpath, '*' + ext))
                for ext in python_extensions)
    files = itertools.chain.from_iterable(expanded)
    names = set(os.path.splitext(os.path.basename(fn))[0] for fn in files)
    for name in names:
        if name != '__init__':
            plugin = load_plugin(name)
            if hasattr(plugin, 'plugin_init'):
                obj = plugin.plugin_init(plugins)
                plugins.append(obj or plugin)
            else:
                plugins.append(plugin)


class LogCatcher(logging.Handler):
    """Logging handler for collecting logged messages so you can check them later"""
    def __init__(self):
        self.messages = []
        logging.Handler.__init__(self, logging.WARNING)
    def emit(self, record):
        self.messages.append(bb.build.logformatter.format(record))
    def contains(self, message):
        return (message in self.messages)

def is_semver(version):
    """
        Is the version string following the semver semantic?

        https://semver.org/spec/v2.0.0.html
    """
    regex = re.compile(
    r"""
    ^
    (0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)
    (?:-(
        (?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)
        (?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*
    ))?
    (?:\+(
        [0-9a-zA-Z-]+
        (?:\.[0-9a-zA-Z-]+)*
    ))?
    $
    """, re.VERBOSE)

    if regex.match(version) is None:
        return False

    return True

# Wrapper around os.rename which can handle cross device problems
# e.g. from container filesystems
def rename(src, dst):
    try:
        os.rename(src, dst)
    except OSError as err:
        if err.errno == 18:
            # Invalid cross-device link error
            shutil.move(src, dst)
        else:
            raise err

@contextmanager
def environment(**envvars):
    """
    Context manager to selectively update the environment with the specified mapping.
    """
    backup = dict(os.environ)
    try:
        os.environ.update(envvars)
        yield
    finally:
        for var in envvars:
            if var in backup:
                os.environ[var] = backup[var]
            elif var in os.environ:
                del os.environ[var]

def is_local_uid(uid=''):
    """
    Check whether uid is a local one or not.
    Can't use pwd module since it gets all UIDs, not local ones only.
    """
    if not uid:
        uid = os.getuid()
    with open('/etc/passwd', 'r') as f:
        for line in f:
            line_split = line.split(':')
            if len(line_split) < 3:
                continue
            if str(uid) == line_split[2]:
                return True
    return False

def mkstemp(suffix=None, prefix=None, dir=None, text=False):
    """
    Generates a unique filename, independent of time.

    mkstemp() in glibc (at least) generates unique file names based on the
    current system time. When combined with highly parallel builds, and
    operating over NFS (e.g. shared sstate/downloads) this can result in
    conflicts and race conditions.

    This function adds additional entropy to the file name so that a collision
    is independent of time and thus extremely unlikely.
    """
    entropy = "".join(random.choices("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", k=20))
    if prefix:
        prefix = prefix + entropy
    else:
        prefix = tempfile.gettempprefix() + entropy
    return tempfile.mkstemp(suffix=suffix, prefix=prefix, dir=dir, text=text)

def path_is_descendant(descendant, ancestor):
    """
    Returns True if the path `descendant` is a descendant of `ancestor`
    (including being equivalent to `ancestor` itself). Otherwise returns False.
    Correctly accounts for symlinks, bind mounts, etc. by using
    os.path.samestat() to compare paths

    May raise any exception that os.stat() raises
    """

    ancestor_stat = os.stat(ancestor)

    # Recurse up each directory component of the descendant to see if it is
    # equivalent to the ancestor
    check_dir = os.path.abspath(descendant).rstrip("/")
    while check_dir:
        check_stat = os.stat(check_dir)
        if os.path.samestat(check_stat, ancestor_stat):
            return True
        check_dir = os.path.dirname(check_dir).rstrip("/")

    return False

# If we don't have a timeout of some kind and a process/thread exits badly (for example
# OOM killed) and held a lock, we'd just hang in the lock futex forever. It is better
# we exit at some point than hang. 5 minutes with no progress means we're probably deadlocked.
@contextmanager
def lock_timeout(lock):
    held = lock.acquire(timeout=5*60)
    try:
        if not held:
            os._exit(1)
        yield held
    finally:
        lock.release()
