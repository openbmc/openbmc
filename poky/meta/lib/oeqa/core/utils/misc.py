#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

def toList(obj, obj_type, obj_name="Object"):
    if isinstance(obj, obj_type):
        return [obj]
    elif isinstance(obj, list):
        return obj
    else:
        raise TypeError("%s must be %s or list" % (obj_name, obj_type))

def toSet(obj, obj_type, obj_name="Object"):
    if isinstance(obj, obj_type):
        return {obj}
    elif isinstance(obj, list):
        return set(obj)
    elif isinstance(obj, set):
        return obj
    else:
        raise TypeError("%s must be %s or set" % (obj_name, obj_type))

def strToList(obj, obj_name="Object"):
    return toList(obj, str, obj_name)

def strToSet(obj, obj_name="Object"):
    return toSet(obj, str, obj_name)

def intToList(obj, obj_name="Object"):
    return toList(obj, int, obj_name)

def dataStoteToDict(d, variables):
    data = {}

    for v in variables:
        data[v] = d.getVar(v)

    return data

def updateTestData(d, td, variables):
    """
    Updates variables with values of data store to test data.
    """
    for var in variables:
        td[var] = d.getVar(var)
