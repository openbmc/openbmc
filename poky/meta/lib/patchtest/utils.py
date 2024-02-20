# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# utils: common methods used by the patchtest framework
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import subprocess
import logging
import re
import mailbox

class CmdException(Exception):
    """ Simple exception class where its attributes are the ones passed when instantiated """
    def __init__(self, cmd):
        self._cmd = cmd
    def __getattr__(self, name):
        value = None
        if self._cmd.has_key(name):
            value = self._cmd[name]
        return value

def exec_cmd(cmd, cwd, ignore_error=False, input=None, strip=True, updateenv={}):
    """
         Input:

            cmd: dict containing the following keys:

                cmd : the command itself as an array of strings
                ignore_error: if False, no exception is raised
                strip: indicates if strip is done on the output (stdout and stderr)
                input: input data to the command (stdin)
                updateenv: environment variables to be appended to the current
                process environment variables

            NOTE: keys 'ignore_error' and 'input' are optional; if not included,
            the defaults are the ones specify in the arguments
            cwd: directory where commands are executed
            ignore_error: raise CmdException if command fails to execute and
            this value is False
            input: input data (stdin) for the command

         Output: dict containing the following keys:

             cmd: the same as input
             ignore_error: the same as input
             strip: the same as input
             input: the same as input
             stdout: Standard output after command's execution
             stderr: Standard error after command's execution
             returncode: Return code after command's execution

    """
    cmddefaults = {
        'cmd':'',
        'ignore_error':ignore_error,
        'strip':strip,
        'input':input,
        'updateenv':updateenv,
    }

    # update input values if necessary
    cmddefaults.update(cmd)

    _cmd = cmddefaults

    if not _cmd['cmd']:
        raise CmdException({'cmd':None, 'stderr':'no command given'})

    # update the environment
    env = os.environ
    env.update(_cmd['updateenv'])

    _command = [e for e in _cmd['cmd']]
    p = subprocess.Popen(_command,
                         stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE,
                         universal_newlines=True,
                         cwd=cwd,
                         env=env)

    # execute the command and strip output
    (_stdout, _stderr) = p.communicate(_cmd['input'])
    if _cmd['strip']:
        _stdout, _stderr = map(str.strip, [_stdout, _stderr])

    # generate the result
    result = _cmd
    result.update({'cmd':_command,'stdout':_stdout,'stderr':_stderr,'returncode':p.returncode})

    # launch exception if necessary
    if not _cmd['ignore_error'] and p.returncode:
        raise CmdException(result)

    return result

def exec_cmds(cmds, cwd):
    """ Executes commands

         Input:
             cmds: Array of commands
             cwd: directory where commands are executed

         Output: Array of output commands
    """
    results = []
    _cmds = cmds

    for cmd in _cmds:
        result = exec_cmd(cmd, cwd)
        results.append(result)

    return results

def logger_create(name):
    logger = logging.getLogger(name)
    loggerhandler = logging.StreamHandler()
    loggerhandler.setFormatter(logging.Formatter("%(message)s"))
    logger.addHandler(loggerhandler)
    logger.setLevel(logging.INFO)
    return logger

def get_subject_prefix(path):
    prefix = ""
    mbox = mailbox.mbox(path)

    if len(mbox):
        subject = mbox[0]['subject']
        if subject:
            pattern = re.compile(r"(\[.*\])", re.DOTALL)
            match = pattern.search(subject)
            if match:
                prefix = match.group(1)

    return prefix

def valid_branch(branch):
    """ Check if branch is valid name """
    lbranch = branch.lower()

    invalid  = lbranch.startswith('patch') or \
               lbranch.startswith('rfc') or \
               lbranch.startswith('resend') or \
               re.search(r'^v\d+', lbranch) or \
               re.search(r'^\d+/\d+', lbranch)

    return not invalid

def get_branch(path):
    """ Get the branch name from mbox """
    fullprefix = get_subject_prefix(path)
    branch, branches, valid_branches = None, [], []

    if fullprefix:
        prefix = fullprefix.strip('[]')
        branches = [ b.strip() for b in prefix.split(',')]
        valid_branches = [b for b in branches if valid_branch(b)]

    if len(valid_branches):
        branch = valid_branches[0]

    return branch

