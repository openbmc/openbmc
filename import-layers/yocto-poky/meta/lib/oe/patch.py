import oe.path

class NotFoundError(bb.BBHandledException):
    def __init__(self, path):
        self.path = path

    def __str__(self):
        return "Error: %s not found." % self.path

class CmdError(bb.BBHandledException):
    def __init__(self, command, exitstatus, output):
        self.command = command
        self.status = exitstatus
        self.output = output

    def __str__(self):
        return "Command Error: '%s' exited with %d  Output:\n%s" % \
                (self.command, self.status, self.output)


def runcmd(args, dir = None):
    import pipes

    if dir:
        olddir = os.path.abspath(os.curdir)
        if not os.path.exists(dir):
            raise NotFoundError(dir)
        os.chdir(dir)
        # print("cwd: %s -> %s" % (olddir, dir))

    try:
        args = [ pipes.quote(str(arg)) for arg in args ]
        cmd = " ".join(args)
        # print("cmd: %s" % cmd)
        (exitstatus, output) = oe.utils.getstatusoutput(cmd)
        if exitstatus != 0:
            raise CmdError(cmd, exitstatus >> 8, output)
        return output

    finally:
        if dir:
            os.chdir(olddir)

class PatchError(Exception):
    def __init__(self, msg):
        self.msg = msg

    def __str__(self):
        return "Patch Error: %s" % self.msg

class PatchSet(object):
    defaults = {
        "strippath": 1
    }

    def __init__(self, dir, d):
        self.dir = dir
        self.d = d
        self.patches = []
        self._current = None

    def current(self):
        return self._current

    def Clean(self):
        """
        Clean out the patch set.  Generally includes unapplying all
        patches and wiping out all associated metadata.
        """
        raise NotImplementedError()

    def Import(self, patch, force):
        if not patch.get("file"):
            if not patch.get("remote"):
                raise PatchError("Patch file must be specified in patch import.")
            else:
                patch["file"] = bb.fetch2.localpath(patch["remote"], self.d)

        for param in PatchSet.defaults:
            if not patch.get(param):
                patch[param] = PatchSet.defaults[param]

        if patch.get("remote"):
            patch["file"] = self.d.expand(bb.fetch2.localpath(patch["remote"], self.d))

        patch["filemd5"] = bb.utils.md5_file(patch["file"])

    def Push(self, force):
        raise NotImplementedError()

    def Pop(self, force):
        raise NotImplementedError()

    def Refresh(self, remote = None, all = None):
        raise NotImplementedError()

    @staticmethod
    def getPatchedFiles(patchfile, striplevel, srcdir=None):
        """
        Read a patch file and determine which files it will modify.
        Params:
            patchfile: the patch file to read
            striplevel: the strip level at which the patch is going to be applied
            srcdir: optional path to join onto the patched file paths
        Returns:
            A list of tuples of file path and change mode ('A' for add,
            'D' for delete or 'M' for modify)
        """

        def patchedpath(patchline):
            filepth = patchline.split()[1]
            if filepth.endswith('/dev/null'):
                return '/dev/null'
            filesplit = filepth.split(os.sep)
            if striplevel > len(filesplit):
                bb.error('Patch %s has invalid strip level %d' % (patchfile, striplevel))
                return None
            return os.sep.join(filesplit[striplevel:])

        for encoding in ['utf-8', 'latin-1']:
            try:
                copiedmode = False
                filelist = []
                with open(patchfile) as f:
                    for line in f:
                        if line.startswith('--- '):
                            patchpth = patchedpath(line)
                            if not patchpth:
                                break
                            if copiedmode:
                                addedfile = patchpth
                            else:
                                removedfile = patchpth
                        elif line.startswith('+++ '):
                            addedfile = patchedpath(line)
                            if not addedfile:
                                break
                        elif line.startswith('*** '):
                            copiedmode = True
                            removedfile = patchedpath(line)
                            if not removedfile:
                                break
                        else:
                            removedfile = None
                            addedfile = None

                        if addedfile and removedfile:
                            if removedfile == '/dev/null':
                                mode = 'A'
                            elif addedfile == '/dev/null':
                                mode = 'D'
                            else:
                                mode = 'M'
                            if srcdir:
                                fullpath = os.path.abspath(os.path.join(srcdir, addedfile))
                            else:
                                fullpath = addedfile
                            filelist.append((fullpath, mode))
            except UnicodeDecodeError:
                continue
            break
        else:
            raise PatchError('Unable to decode %s' % patchfile)

        return filelist


class PatchTree(PatchSet):
    def __init__(self, dir, d):
        PatchSet.__init__(self, dir, d)
        self.patchdir = os.path.join(self.dir, 'patches')
        self.seriespath = os.path.join(self.dir, 'patches', 'series')
        bb.utils.mkdirhier(self.patchdir)

    def _appendPatchFile(self, patch, strippath):
        with open(self.seriespath, 'a') as f:
            f.write(os.path.basename(patch) + "," + strippath + "\n")
        shellcmd = ["cat", patch, ">" , self.patchdir + "/" + os.path.basename(patch)]
        runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)

    def _removePatch(self, p):
        patch = {}
        patch['file'] = p.split(",")[0]
        patch['strippath'] = p.split(",")[1]
        self._applypatch(patch, False, True)

    def _removePatchFile(self, all = False):
        if not os.path.exists(self.seriespath):
            return
        with open(self.seriespath, 'r+') as f:
            patches = f.readlines()
        if all:
            for p in reversed(patches):
                self._removePatch(os.path.join(self.patchdir, p.strip()))
            patches = []
        else:
            self._removePatch(os.path.join(self.patchdir, patches[-1].strip()))
            patches.pop()
        with open(self.seriespath, 'w') as f:
            for p in patches:
                f.write(p)
         
    def Import(self, patch, force = None):
        """"""
        PatchSet.Import(self, patch, force)

        if self._current is not None:
            i = self._current + 1
        else:
            i = 0
        self.patches.insert(i, patch)

    def _applypatch(self, patch, force = False, reverse = False, run = True):
        shellcmd = ["cat", patch['file'], "|", "patch", "-p", patch['strippath']]
        if reverse:
            shellcmd.append('-R')

        if not run:
            return "sh" + "-c" + " ".join(shellcmd)

        if not force:
            shellcmd.append('--dry-run')

        try:
            output = runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)

            if force:
                return

            shellcmd.pop(len(shellcmd) - 1)
            output = runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
        except CmdError as err:
            raise bb.BBHandledException("Applying '%s' failed:\n%s" %
                                        (os.path.basename(patch['file']), err.output))

        if not reverse:
            self._appendPatchFile(patch['file'], patch['strippath'])

        return output

    def Push(self, force = False, all = False, run = True):
        bb.note("self._current is %s" % self._current)
        bb.note("patches is %s" % self.patches)
        if all:
            for i in self.patches:
                bb.note("applying patch %s" % i)
                self._applypatch(i, force)
                self._current = i
        else:
            if self._current is not None:
                next = self._current + 1
            else:
                next = 0

            bb.note("applying patch %s" % self.patches[next])
            ret = self._applypatch(self.patches[next], force)

            self._current = next
            return ret

    def Pop(self, force = None, all = None):
        if all:
            self._removePatchFile(True)
            self._current = None
        else:
            self._removePatchFile(False)

        if self._current == 0:
            self._current = None

        if self._current is not None:
            self._current = self._current - 1

    def Clean(self):
        """"""
        self.Pop(all=True)

class GitApplyTree(PatchTree):
    patch_line_prefix = '%% original patch'
    ignore_commit_prefix = '%% ignore'

    def __init__(self, dir, d):
        PatchTree.__init__(self, dir, d)
        self.commituser = d.getVar('PATCH_GIT_USER_NAME')
        self.commitemail = d.getVar('PATCH_GIT_USER_EMAIL')

    @staticmethod
    def extractPatchHeader(patchfile):
        """
        Extract just the header lines from the top of a patch file
        """
        for encoding in ['utf-8', 'latin-1']:
            lines = []
            try:
                with open(patchfile, 'r', encoding=encoding) as f:
                    for line in f:
                        if line.startswith('Index: ') or line.startswith('diff -') or line.startswith('---'):
                            break
                        lines.append(line)
            except UnicodeDecodeError:
                continue
            break
        else:
            raise PatchError('Unable to find a character encoding to decode %s' % patchfile)
        return lines

    @staticmethod
    def decodeAuthor(line):
        from email.header import decode_header
        authorval = line.split(':', 1)[1].strip().replace('"', '')
        result =  decode_header(authorval)[0][0]
        if hasattr(result, 'decode'):
            result = result.decode('utf-8')
        return result

    @staticmethod
    def interpretPatchHeader(headerlines):
        import re
        author_re = re.compile('[\S ]+ <\S+@\S+\.\S+>')
        from_commit_re = re.compile('^From [a-z0-9]{40} .*')
        outlines = []
        author = None
        date = None
        subject = None
        for line in headerlines:
            if line.startswith('Subject: '):
                subject = line.split(':', 1)[1]
                # Remove any [PATCH][oe-core] etc.
                subject = re.sub(r'\[.+?\]\s*', '', subject)
                continue
            elif line.startswith('From: ') or line.startswith('Author: '):
                authorval = GitApplyTree.decodeAuthor(line)
                # git is fussy about author formatting i.e. it must be Name <email@domain>
                if author_re.match(authorval):
                    author = authorval
                    continue
            elif line.startswith('Date: '):
                if date is None:
                    dateval = line.split(':', 1)[1].strip()
                    # Very crude check for date format, since git will blow up if it's not in the right
                    # format. Without e.g. a python-dateutils dependency we can't do a whole lot more
                    if len(dateval) > 12:
                        date = dateval
                continue
            elif not author and line.lower().startswith('signed-off-by: '):
                authorval = GitApplyTree.decodeAuthor(line)
                # git is fussy about author formatting i.e. it must be Name <email@domain>
                if author_re.match(authorval):
                    author = authorval
            elif from_commit_re.match(line):
                # We don't want the From <commit> line - if it's present it will break rebasing
                continue
            outlines.append(line)

        if not subject:
            firstline = None
            for line in headerlines:
                line = line.strip()
                if firstline:
                    if line:
                        # Second line is not blank, the first line probably isn't usable
                        firstline = None
                    break
                elif line:
                    firstline = line
            if firstline and not firstline.startswith(('#', 'Index:', 'Upstream-Status:')) and len(firstline) < 100:
                subject = firstline

        return outlines, author, date, subject

    @staticmethod
    def gitCommandUserOptions(cmd, commituser=None, commitemail=None, d=None):
        if d:
            commituser = d.getVar('PATCH_GIT_USER_NAME')
            commitemail = d.getVar('PATCH_GIT_USER_EMAIL')
        if commituser:
            cmd += ['-c', 'user.name="%s"' % commituser]
        if commitemail:
            cmd += ['-c', 'user.email="%s"' % commitemail]

    @staticmethod
    def prepareCommit(patchfile, commituser=None, commitemail=None):
        """
        Prepare a git commit command line based on the header from a patch file
        (typically this is useful for patches that cannot be applied with "git am" due to formatting)
        """
        import tempfile
        # Process patch header and extract useful information
        lines = GitApplyTree.extractPatchHeader(patchfile)
        outlines, author, date, subject = GitApplyTree.interpretPatchHeader(lines)
        if not author or not subject or not date:
            try:
                shellcmd = ["git", "log", "--format=email", "--follow", "--diff-filter=A", "--", patchfile]
                out = runcmd(["sh", "-c", " ".join(shellcmd)], os.path.dirname(patchfile))
            except CmdError:
                out = None
            if out:
                _, newauthor, newdate, newsubject = GitApplyTree.interpretPatchHeader(out.splitlines())
                if not author:
                    # If we're setting the author then the date should be set as well
                    author = newauthor
                    date = newdate
                elif not date:
                    # If we don't do this we'll get the current date, at least this will be closer
                    date = newdate
                if not subject:
                    subject = newsubject
        if subject and outlines and not outlines[0].strip() == subject:
            outlines.insert(0, '%s\n\n' % subject.strip())

        # Write out commit message to a file
        with tempfile.NamedTemporaryFile('w', delete=False) as tf:
            tmpfile = tf.name
            for line in outlines:
                tf.write(line)
        # Prepare git command
        cmd = ["git"]
        GitApplyTree.gitCommandUserOptions(cmd, commituser, commitemail)
        cmd += ["commit", "-F", tmpfile]
        # git doesn't like plain email addresses as authors
        if author and '<' in author:
            cmd.append('--author="%s"' % author)
        if date:
            cmd.append('--date="%s"' % date)
        return (tmpfile, cmd)

    @staticmethod
    def extractPatches(tree, startcommit, outdir, paths=None):
        import tempfile
        import shutil
        import re
        tempdir = tempfile.mkdtemp(prefix='oepatch')
        try:
            shellcmd = ["git", "format-patch", startcommit, "-o", tempdir]
            if paths:
                shellcmd.append('--')
                shellcmd.extend(paths)
            out = runcmd(["sh", "-c", " ".join(shellcmd)], tree)
            if out:
                for srcfile in out.split():
                    for encoding in ['utf-8', 'latin-1']:
                        patchlines = []
                        outfile = None
                        try:
                            with open(srcfile, 'r', encoding=encoding) as f:
                                for line in f:
                                    checkline = line
                                    if checkline.startswith('Subject: '):
                                        checkline = re.sub(r'\[.+?\]\s*', '', checkline[9:])
                                    if checkline.startswith(GitApplyTree.patch_line_prefix):
                                        outfile = line.split()[-1].strip()
                                        continue
                                    if checkline.startswith(GitApplyTree.ignore_commit_prefix):
                                        continue
                                    patchlines.append(line)
                        except UnicodeDecodeError:
                            continue
                        break
                    else:
                        raise PatchError('Unable to find a character encoding to decode %s' % srcfile)

                    if not outfile:
                        outfile = os.path.basename(srcfile)
                    with open(os.path.join(outdir, outfile), 'w') as of:
                        for line in patchlines:
                            of.write(line)
        finally:
            shutil.rmtree(tempdir)

    def _applypatch(self, patch, force = False, reverse = False, run = True):
        import shutil

        def _applypatchhelper(shellcmd, patch, force = False, reverse = False, run = True):
            if reverse:
                shellcmd.append('-R')

            shellcmd.append(patch['file'])

            if not run:
                return "sh" + "-c" + " ".join(shellcmd)

            return runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)

        # Add hooks which add a pointer to the original patch file name in the commit message
        reporoot = (runcmd("git rev-parse --show-toplevel".split(), self.dir) or '').strip()
        if not reporoot:
            raise Exception("Cannot get repository root for directory %s" % self.dir)
        hooks_dir = os.path.join(reporoot, '.git', 'hooks')
        hooks_dir_backup = hooks_dir + '.devtool-orig'
        if os.path.lexists(hooks_dir_backup):
            raise Exception("Git hooks backup directory already exists: %s" % hooks_dir_backup)
        if os.path.lexists(hooks_dir):
            shutil.move(hooks_dir, hooks_dir_backup)
        os.mkdir(hooks_dir)
        commithook = os.path.join(hooks_dir, 'commit-msg')
        applyhook = os.path.join(hooks_dir, 'applypatch-msg')
        with open(commithook, 'w') as f:
            # NOTE: the formatting here is significant; if you change it you'll also need to
            # change other places which read it back
            f.write('echo >> $1\n')
            f.write('echo "%s: $PATCHFILE" >> $1\n' % GitApplyTree.patch_line_prefix)
        os.chmod(commithook, 0o755)
        shutil.copy2(commithook, applyhook)
        try:
            patchfilevar = 'PATCHFILE="%s"' % os.path.basename(patch['file'])
            try:
                shellcmd = [patchfilevar, "git", "--work-tree=%s" % reporoot]
                self.gitCommandUserOptions(shellcmd, self.commituser, self.commitemail)
                shellcmd += ["am", "-3", "--keep-cr", "-p%s" % patch['strippath']]
                return _applypatchhelper(shellcmd, patch, force, reverse, run)
            except CmdError:
                # Need to abort the git am, or we'll still be within it at the end
                try:
                    shellcmd = ["git", "--work-tree=%s" % reporoot, "am", "--abort"]
                    runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
                except CmdError:
                    pass
                # git am won't always clean up after itself, sadly, so...
                shellcmd = ["git", "--work-tree=%s" % reporoot, "reset", "--hard", "HEAD"]
                runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
                # Also need to take care of any stray untracked files
                shellcmd = ["git", "--work-tree=%s" % reporoot, "clean", "-f"]
                runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)

                # Fall back to git apply
                shellcmd = ["git", "--git-dir=%s" % reporoot, "apply", "-p%s" % patch['strippath']]
                try:
                    output = _applypatchhelper(shellcmd, patch, force, reverse, run)
                except CmdError:
                    # Fall back to patch
                    output = PatchTree._applypatch(self, patch, force, reverse, run)
                # Add all files
                shellcmd = ["git", "add", "-f", "-A", "."]
                output += runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
                # Exclude the patches directory
                shellcmd = ["git", "reset", "HEAD", self.patchdir]
                output += runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
                # Commit the result
                (tmpfile, shellcmd) = self.prepareCommit(patch['file'], self.commituser, self.commitemail)
                try:
                    shellcmd.insert(0, patchfilevar)
                    output += runcmd(["sh", "-c", " ".join(shellcmd)], self.dir)
                finally:
                    os.remove(tmpfile)
                return output
        finally:
            shutil.rmtree(hooks_dir)
            if os.path.lexists(hooks_dir_backup):
                shutil.move(hooks_dir_backup, hooks_dir)


class QuiltTree(PatchSet):
    def _runcmd(self, args, run = True):
        quiltrc = self.d.getVar('QUILTRCFILE')
        if not run:
            return ["quilt"] + ["--quiltrc"] + [quiltrc] + args
        runcmd(["quilt"] + ["--quiltrc"] + [quiltrc] + args, self.dir)

    def _quiltpatchpath(self, file):
        return os.path.join(self.dir, "patches", os.path.basename(file))


    def __init__(self, dir, d):
        PatchSet.__init__(self, dir, d)
        self.initialized = False
        p = os.path.join(self.dir, 'patches')
        if not os.path.exists(p):
            os.makedirs(p)

    def Clean(self):
        try:
            self._runcmd(["pop", "-a", "-f"])
            oe.path.remove(os.path.join(self.dir, "patches","series"))
        except Exception:
            pass
        self.initialized = True

    def InitFromDir(self):
        # read series -> self.patches
        seriespath = os.path.join(self.dir, 'patches', 'series')
        if not os.path.exists(self.dir):
            raise NotFoundError(self.dir)
        if os.path.exists(seriespath):
            with open(seriespath, 'r') as f:
                for line in f.readlines():
                    patch = {}
                    parts = line.strip().split()
                    patch["quiltfile"] = self._quiltpatchpath(parts[0])
                    patch["quiltfilemd5"] = bb.utils.md5_file(patch["quiltfile"])
                    if len(parts) > 1:
                        patch["strippath"] = parts[1][2:]
                    self.patches.append(patch)

            # determine which patches are applied -> self._current
            try:
                output = runcmd(["quilt", "applied"], self.dir)
            except CmdError:
                import sys
                if sys.exc_value.output.strip() == "No patches applied":
                    return
                else:
                    raise
            output = [val for val in output.split('\n') if not val.startswith('#')]
            for patch in self.patches:
                if os.path.basename(patch["quiltfile"]) == output[-1]:
                    self._current = self.patches.index(patch)
        self.initialized = True

    def Import(self, patch, force = None):
        if not self.initialized:
            self.InitFromDir()
        PatchSet.Import(self, patch, force)
        oe.path.symlink(patch["file"], self._quiltpatchpath(patch["file"]), force=True)
        with open(os.path.join(self.dir, "patches", "series"), "a") as f:
            f.write(os.path.basename(patch["file"]) + " -p" + patch["strippath"] + "\n")
        patch["quiltfile"] = self._quiltpatchpath(patch["file"])
        patch["quiltfilemd5"] = bb.utils.md5_file(patch["quiltfile"])

        # TODO: determine if the file being imported:
        #      1) is already imported, and is the same
        #      2) is already imported, but differs

        self.patches.insert(self._current or 0, patch)


    def Push(self, force = False, all = False, run = True):
        # quilt push [-f]

        args = ["push"]
        if force:
            args.append("-f")
        if all:
            args.append("-a")
        if not run:
            return self._runcmd(args, run)

        self._runcmd(args)

        if self._current is not None:
            self._current = self._current + 1
        else:
            self._current = 0

    def Pop(self, force = None, all = None):
        # quilt pop [-f]
        args = ["pop"]
        if force:
            args.append("-f")
        if all:
            args.append("-a")

        self._runcmd(args)

        if self._current == 0:
            self._current = None

        if self._current is not None:
            self._current = self._current - 1

    def Refresh(self, **kwargs):
        if kwargs.get("remote"):
            patch = self.patches[kwargs["patch"]]
            if not patch:
                raise PatchError("No patch found at index %s in patchset." % kwargs["patch"])
            (type, host, path, user, pswd, parm) = bb.fetch.decodeurl(patch["remote"])
            if type == "file":
                import shutil
                if not patch.get("file") and patch.get("remote"):
                    patch["file"] = bb.fetch2.localpath(patch["remote"], self.d)

                shutil.copyfile(patch["quiltfile"], patch["file"])
            else:
                raise PatchError("Unable to do a remote refresh of %s, unsupported remote url scheme %s." % (os.path.basename(patch["quiltfile"]), type))
        else:
            # quilt refresh
            args = ["refresh"]
            if kwargs.get("quiltfile"):
                args.append(os.path.basename(kwargs["quiltfile"]))
            elif kwargs.get("patch"):
                args.append(os.path.basename(self.patches[kwargs["patch"]]["quiltfile"]))
            self._runcmd(args)

class Resolver(object):
    def __init__(self, patchset, terminal):
        raise NotImplementedError()

    def Resolve(self):
        raise NotImplementedError()

    def Revert(self):
        raise NotImplementedError()

    def Finalize(self):
        raise NotImplementedError()

class NOOPResolver(Resolver):
    def __init__(self, patchset, terminal):
        self.patchset = patchset
        self.terminal = terminal

    def Resolve(self):
        olddir = os.path.abspath(os.curdir)
        os.chdir(self.patchset.dir)
        try:
            self.patchset.Push()
        except Exception:
            import sys
            os.chdir(olddir)
            raise

# Patch resolver which relies on the user doing all the work involved in the
# resolution, with the exception of refreshing the remote copy of the patch
# files (the urls).
class UserResolver(Resolver):
    def __init__(self, patchset, terminal):
        self.patchset = patchset
        self.terminal = terminal

    # Force a push in the patchset, then drop to a shell for the user to
    # resolve any rejected hunks
    def Resolve(self):
        olddir = os.path.abspath(os.curdir)
        os.chdir(self.patchset.dir)
        try:
            self.patchset.Push(False)
        except CmdError as v:
            # Patch application failed
            patchcmd = self.patchset.Push(True, False, False)

            t = self.patchset.d.getVar('T')
            if not t:
                bb.msg.fatal("Build", "T not set")
            bb.utils.mkdirhier(t)
            import random
            rcfile = "%s/bashrc.%s.%s" % (t, str(os.getpid()), random.random())
            with open(rcfile, "w") as f:
                f.write("echo '*** Manual patch resolution mode ***'\n")
                f.write("echo 'Dropping to a shell, so patch rejects can be fixed manually.'\n")
                f.write("echo 'Run \"quilt refresh\" when patch is corrected, press CTRL+D to exit.'\n")
                f.write("echo ''\n")
                f.write(" ".join(patchcmd) + "\n")
            os.chmod(rcfile, 0o775)

            self.terminal("bash --rcfile " + rcfile, 'Patch Rejects: Please fix patch rejects manually', self.patchset.d)

            # Construct a new PatchSet after the user's changes, compare the
            # sets, checking patches for modifications, and doing a remote
            # refresh on each.
            oldpatchset = self.patchset
            self.patchset = oldpatchset.__class__(self.patchset.dir, self.patchset.d)

            for patch in self.patchset.patches:
                oldpatch = None
                for opatch in oldpatchset.patches:
                    if opatch["quiltfile"] == patch["quiltfile"]:
                        oldpatch = opatch

                if oldpatch:
                    patch["remote"] = oldpatch["remote"]
                    if patch["quiltfile"] == oldpatch["quiltfile"]:
                        if patch["quiltfilemd5"] != oldpatch["quiltfilemd5"]:
                            bb.note("Patch %s has changed, updating remote url %s" % (os.path.basename(patch["quiltfile"]), patch["remote"]))
                            # user change?  remote refresh
                            self.patchset.Refresh(remote=True, patch=self.patchset.patches.index(patch))
                        else:
                            # User did not fix the problem.  Abort.
                            raise PatchError("Patch application failed, and user did not fix and refresh the patch.")
        except Exception:
            os.chdir(olddir)
            raise
        os.chdir(olddir)


def patch_path(url, fetch, workdir, expand=True):
    """Return the local path of a patch, or None if this isn't a patch"""

    local = fetch.localpath(url)
    base, ext = os.path.splitext(os.path.basename(local))
    if ext in ('.gz', '.bz2', '.xz', '.Z'):
        if expand:
            local = os.path.join(workdir, base)
        ext = os.path.splitext(base)[1]

    urldata = fetch.ud[url]
    if "apply" in urldata.parm:
        apply = oe.types.boolean(urldata.parm["apply"])
        if not apply:
            return
    elif ext not in (".diff", ".patch"):
        return

    return local

def src_patches(d, all=False, expand=True):
    workdir = d.getVar('WORKDIR')
    fetch = bb.fetch2.Fetch([], d)
    patches = []
    sources = []
    for url in fetch.urls:
        local = patch_path(url, fetch, workdir, expand)
        if not local:
            if all:
                local = fetch.localpath(url)
                sources.append(local)
            continue

        urldata = fetch.ud[url]
        parm = urldata.parm
        patchname = parm.get('pname') or os.path.basename(local)

        apply, reason = should_apply(parm, d)
        if not apply:
            if reason:
                bb.note("Patch %s %s" % (patchname, reason))
            continue

        patchparm = {'patchname': patchname}
        if "striplevel" in parm:
            striplevel = parm["striplevel"]
        elif "pnum" in parm:
            #bb.msg.warn(None, "Deprecated usage of 'pnum' url parameter in '%s', please use 'striplevel'" % url)
            striplevel = parm["pnum"]
        else:
            striplevel = '1'
        patchparm['striplevel'] = striplevel

        patchdir = parm.get('patchdir')
        if patchdir:
            patchparm['patchdir'] = patchdir

        localurl = bb.fetch.encodeurl(('file', '', local, '', '', patchparm))
        patches.append(localurl)

    if all:
        return sources

    return patches


def should_apply(parm, d):
    if "mindate" in parm or "maxdate" in parm:
        pn = d.getVar('PN')
        srcdate = d.getVar('SRCDATE_%s' % pn)
        if not srcdate:
            srcdate = d.getVar('SRCDATE')

        if srcdate == "now":
            srcdate = d.getVar('DATE')

        if "maxdate" in parm and parm["maxdate"] < srcdate:
            return False, 'is outdated'

        if "mindate" in parm and parm["mindate"] > srcdate:
            return False, 'is predated'


    if "minrev" in parm:
        srcrev = d.getVar('SRCREV')
        if srcrev and srcrev < parm["minrev"]:
            return False, 'applies to later revisions'

    if "maxrev" in parm:
        srcrev = d.getVar('SRCREV')
        if srcrev and srcrev > parm["maxrev"]:
            return False, 'applies to earlier revisions'

    if "rev" in parm:
        srcrev = d.getVar('SRCREV')
        if srcrev and parm["rev"] not in srcrev:
            return False, "doesn't apply to revision"

    if "notrev" in parm:
        srcrev = d.getVar('SRCREV')
        if srcrev and parm["notrev"] in srcrev:
            return False, "doesn't apply to revision"

    return True, None

