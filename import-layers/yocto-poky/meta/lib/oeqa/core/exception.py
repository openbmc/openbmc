# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

class OEQAException(Exception):
    pass

class OEQATimeoutError(OEQAException):
    pass

class OEQAMissingVariable(OEQAException):
    pass

class OEQADependency(OEQAException):
    pass
