import errno
import glob
import shutil
import subprocess
import os.path

def join(*paths):
    """Like os.path.join but doesn't treat absolute RHS specially"""
    return os.path.normpath("/".join(paths))

def relative(src, dest):
    """ Return a relative path from src to dest.

    >>> relative("/usr/bin", "/tmp/foo/bar")
    ../../tmp/foo/bar

    >>> relative("/usr/bin", "/usr/lib")
    ../lib

    >>> relative("/tmp", "/tmp/foo/bar")
    foo/bar
    """

    return os.path.relpath(dest, src)

def make_relative_symlink(path):
    """ Convert an absolute symlink to a relative one """
    if not os.path.islink(path):
        return
    link = os.readlink(path)
    if not os.path.isabs(link):
        return

    # find the common ancestor directory
    ancestor = path
    depth = 0
    while ancestor and not link.startswith(ancestor):
        ancestor = ancestor.rpartition('/')[0]
        depth += 1

    if not ancestor:
        print("make_relative_symlink() Error: unable to find the common ancestor of %s and its target" % path)
        return

    base = link.partition(ancestor)[2].strip('/')
    while depth > 1:
        base = "../" + base
        depth -= 1

    os.remove(path)
    os.symlink(base, path)

def replace_absolute_symlinks(basedir, d):
    """
    Walk basedir looking for absolute symlinks and replacing them with relative ones.
    The absolute links are assumed to be relative to basedir
    (compared to make_relative_symlink above which tries to compute common ancestors
    using pattern matching instead)
    """
    for walkroot, dirs, files in os.walk(basedir):
        for file in files + dirs:
            path = os.path.join(walkroot, file)
            if not os.path.islink(path):
                continue
            link = os.readlink(path)
            if not os.path.isabs(link):
                continue
            walkdir = os.path.dirname(path.rpartition(basedir)[2])
            base = os.path.relpath(link, walkdir)
            bb.debug(2, "Replacing absolute path %s with relative path %s" % (link, base))
            os.remove(path)
            os.symlink(base, path)

def format_display(path, metadata):
    """ Prepare a path for display to the user. """
    rel = relative(metadata.getVar("TOPDIR"), path)
    if len(rel) > len(path):
        return path
    else:
        return rel

def copytree(src, dst):
    # We could use something like shutil.copytree here but it turns out to
    # to be slow. It takes twice as long copying to an empty directory. 
    # If dst already has contents performance can be 15 time slower
    # This way we also preserve hardlinks between files in the tree.

    bb.utils.mkdirhier(dst)
    cmd = "tar --xattrs --xattrs-include='*' -cf - -S -C %s -p . | tar --xattrs --xattrs-include='*' -xf - -C %s" % (src, dst)
    subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)

def copyhardlinktree(src, dst):
    """ Make the hard link when possible, otherwise copy. """
    bb.utils.mkdirhier(dst)
    if os.path.isdir(src) and not len(os.listdir(src)):
        return

    if (os.stat(src).st_dev ==  os.stat(dst).st_dev):
        # Need to copy directories only with tar first since cp will error if two 
        # writers try and create a directory at the same time
        cmd = "cd %s; find . -type d -print | tar --xattrs --xattrs-include='*' -cf - -S -C %s -p --no-recursion --files-from - | tar --xattrs --xattrs-include='*' -xhf - -C %s" % (src, src, dst)
        subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)
        source = ''
        if os.path.isdir(src):
            if len(glob.glob('%s/.??*' % src)) > 0:
                source = './.??* '
            source += './*'
            s_dir = src
        else:
            source = src
            s_dir = os.getcwd()
        cmd = 'cp -afl --preserve=xattr %s %s' % (source, os.path.realpath(dst))
        subprocess.check_output(cmd, shell=True, cwd=s_dir, stderr=subprocess.STDOUT)
    else:
        copytree(src, dst)

def remove(path, recurse=True):
    """
    Equivalent to rm -f or rm -rf
    NOTE: be careful about passing paths that may contain filenames with
    wildcards in them (as opposed to passing an actual wildcarded path) -
    since we use glob.glob() to expand the path. Filenames containing
    square brackets are particularly problematic since the they may not
    actually expand to match the original filename.
    """
    for name in glob.glob(path):
        try:
            os.unlink(name)
        except OSError as exc:
            if recurse and exc.errno == errno.EISDIR:
                shutil.rmtree(name)
            elif exc.errno != errno.ENOENT:
                raise

def symlink(source, destination, force=False):
    """Create a symbolic link"""
    try:
        if force:
            remove(destination)
        os.symlink(source, destination)
    except OSError as e:
        if e.errno != errno.EEXIST or os.readlink(destination) != source:
            raise

def find(dir, **walkoptions):
    """ Given a directory, recurses into that directory,
    returning all files as absolute paths. """

    for root, dirs, files in os.walk(dir, **walkoptions):
        for file in files:
            yield os.path.join(root, file)


## realpath() related functions
def __is_path_below(file, root):
    return (file + os.path.sep).startswith(root)

def __realpath_rel(start, rel_path, root, loop_cnt, assume_dir):
    """Calculates real path of symlink 'start' + 'rel_path' below
    'root'; no part of 'start' below 'root' must contain symlinks. """
    have_dir = True

    for d in rel_path.split(os.path.sep):
        if not have_dir and not assume_dir:
            raise OSError(errno.ENOENT, "no such directory %s" % start)

        if d == os.path.pardir: # '..'
            if len(start) >= len(root):
                # do not follow '..' before root
                start = os.path.dirname(start)
            else:
                # emit warning?
                pass
        else:
            (start, have_dir) = __realpath(os.path.join(start, d),
                                           root, loop_cnt, assume_dir)

        assert(__is_path_below(start, root))

    return start

def __realpath(file, root, loop_cnt, assume_dir):
    while os.path.islink(file) and len(file) >= len(root):
        if loop_cnt == 0:
            raise OSError(errno.ELOOP, file)

        loop_cnt -= 1
        target = os.path.normpath(os.readlink(file))

        if not os.path.isabs(target):
            tdir = os.path.dirname(file)
            assert(__is_path_below(tdir, root))
        else:
            tdir = root

        file = __realpath_rel(tdir, target, root, loop_cnt, assume_dir)

    try:
        is_dir = os.path.isdir(file)
    except:
        is_dir = false

    return (file, is_dir)

def realpath(file, root, use_physdir = True, loop_cnt = 100, assume_dir = False):
    """ Returns the canonical path of 'file' with assuming a
    toplevel 'root' directory. When 'use_physdir' is set, all
    preceding path components of 'file' will be resolved first;
    this flag should be set unless it is guaranteed that there is
    no symlink in the path. When 'assume_dir' is not set, missing
    path components will raise an ENOENT error"""

    root = os.path.normpath(root)
    file = os.path.normpath(file)

    if not root.endswith(os.path.sep):
        # letting root end with '/' makes some things easier
        root = root + os.path.sep

    if not __is_path_below(file, root):
        raise OSError(errno.EINVAL, "file '%s' is not below root" % file)

    try:
        if use_physdir:
            file = __realpath_rel(root, file[(len(root) - 1):], root, loop_cnt, assume_dir)
        else:
            file = __realpath(file, root, loop_cnt, assume_dir)[0]
    except OSError as e:
        if e.errno == errno.ELOOP:
            # make ELOOP more readable; without catching it, there will
            # be printed a backtrace with 100s of OSError exceptions
            # else
            raise OSError(errno.ELOOP,
                          "too much recursions while resolving '%s'; loop in '%s'" %
                          (file, e.strerror))

        raise

    return file

def is_path_parent(possible_parent, *paths):
    """
    Return True if a path is the parent of another, False otherwise.
    Multiple paths to test can be specified in which case all
    specified test paths must be under the parent in order to
    return True.
    """
    def abs_path_trailing(pth):
        pth_abs = os.path.abspath(pth)
        if not pth_abs.endswith(os.sep):
            pth_abs += os.sep
        return pth_abs

    possible_parent_abs = abs_path_trailing(possible_parent)
    if not paths:
        return False
    for path in paths:
        path_abs = abs_path_trailing(path)
        if not path_abs.startswith(possible_parent_abs):
            return False
    return True

def which_wild(pathname, path=None, mode=os.F_OK, *, reverse=False, candidates=False):
    """Search a search path for pathname, supporting wildcards.

    Return all paths in the specific search path matching the wildcard pattern
    in pathname, returning only the first encountered for each file. If
    candidates is True, information on all potential candidate paths are
    included.
    """
    paths = (path or os.environ.get('PATH', os.defpath)).split(':')
    if reverse:
        paths.reverse()

    seen, files = set(), []
    for index, element in enumerate(paths):
        if not os.path.isabs(element):
            element = os.path.abspath(element)

        candidate = os.path.join(element, pathname)
        globbed = glob.glob(candidate)
        if globbed:
            for found_path in sorted(globbed):
                if not os.access(found_path, mode):
                    continue
                rel = os.path.relpath(found_path, element)
                if rel not in seen:
                    seen.add(rel)
                    if candidates:
                        files.append((found_path, [os.path.join(p, rel) for p in paths[:index+1]]))
                    else:
                        files.append(found_path)

    return files

