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

def logger_create(name):
    logger = logging.getLogger(name)
    loggerhandler = logging.StreamHandler()
    loggerhandler.setFormatter(logging.Formatter("%(message)s"))
    logger.addHandler(loggerhandler)
    logger.setLevel(logging.INFO)
    return logger

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
    fullprefix = ""
    mbox = mailbox.mbox(path)

    if len(mbox):
        subject = mbox[0]['subject']
        if subject:
            pattern = re.compile(r"(\[.*\])", re.DOTALL)
            match = pattern.search(subject)
            if match:
                fullprefix = match.group(1)

    branch, branches, valid_branches = None, [], []

    if fullprefix:
        prefix = fullprefix.strip('[]')
        branches = [ b.strip() for b in prefix.split(',')]
        valid_branches = [b for b in branches if valid_branch(b)]

    if len(valid_branches):
        branch = valid_branches[0]

    return branch

