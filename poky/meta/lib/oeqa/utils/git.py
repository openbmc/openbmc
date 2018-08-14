#
# Copyright (C) 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)
#
"""Git repository interactions"""
import os

from oeqa.utils.commands import runCmd


class GitError(Exception):
    """Git error handling"""
    pass

class GitRepo(object):
    """Class representing a Git repository clone"""
    def __init__(self, path, is_topdir=False):
        git_dir = self._run_git_cmd_at(['rev-parse', '--git-dir'], path)
        git_dir = git_dir if os.path.isabs(git_dir) else os.path.join(path, git_dir)
        self.git_dir = os.path.realpath(git_dir)

        if self._run_git_cmd_at(['rev-parse', '--is-bare-repository'], path) == 'true':
            self.bare = True
            self.top_dir = self.git_dir
        else:
            self.bare = False
            self.top_dir = self._run_git_cmd_at(['rev-parse', '--show-toplevel'],
                                                path)
        realpath = os.path.realpath(path)
        if is_topdir and realpath != self.top_dir:
            raise GitError("{} is not a Git top directory".format(realpath))

    @staticmethod
    def _run_git_cmd_at(git_args, cwd, **kwargs):
        """Run git command at a specified directory"""
        git_cmd = 'git ' if isinstance(git_args, str) else ['git']
        git_cmd += git_args
        ret = runCmd(git_cmd, ignore_status=True, cwd=cwd, **kwargs)
        if ret.status:
            cmd_str = git_cmd if isinstance(git_cmd, str) \
                                else ' '.join(git_cmd)
            raise GitError("'{}' failed with exit code {}: {}".format(
                cmd_str, ret.status, ret.output))
        return ret.output.strip()

    @staticmethod
    def init(path, bare=False):
        """Initialize a new Git repository"""
        cmd = ['init']
        if bare:
            cmd.append('--bare')
        GitRepo._run_git_cmd_at(cmd, cwd=path)
        return GitRepo(path, is_topdir=True)

    def run_cmd(self, git_args, env_update=None):
        """Run Git command"""
        env = None
        if env_update:
            env = os.environ.copy()
            env.update(env_update)
        return self._run_git_cmd_at(git_args, self.top_dir, env=env)

    def rev_parse(self, revision):
        """Do git rev-parse"""
        try:
            return self.run_cmd(['rev-parse', '--verify', revision])
        except GitError:
            # Revision does not exist
            return None

    def get_current_branch(self):
        """Get current branch"""
        try:
            # Strip 11 chars, i.e. 'refs/heads' from the beginning
            return self.run_cmd(['symbolic-ref', 'HEAD'])[11:]
        except GitError:
            return None


