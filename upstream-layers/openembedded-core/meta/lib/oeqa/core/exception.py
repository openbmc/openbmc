#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

class OEQAException(Exception):
    pass

class OEQATimeoutError(OEQAException):
    pass

class OEQAMissingVariable(OEQAException):
    pass

class OEQADependency(OEQAException):
    pass

class OEQAMissingManifest(OEQAException):
    pass

class OEQAPreRun(OEQAException):
    pass

class OEQATestNotFound(OEQAException):
    pass
