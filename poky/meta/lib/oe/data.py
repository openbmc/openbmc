import json
import oe.maketype

def typed_value(key, d):
    """Construct a value for the specified metadata variable, using its flags
    to determine the type and parameters for construction."""
    var_type = d.getVarFlag(key, 'type')
    flags = d.getVarFlags(key)
    if flags is not None:
        flags = dict((flag, d.expand(value))
                     for flag, value in list(flags.items()))
    else:
        flags = {}

    try:
        return oe.maketype.create(d.getVar(key) or '', var_type, **flags)
    except (TypeError, ValueError) as exc:
        bb.msg.fatal("Data", "%s: %s" % (key, str(exc)))

def export2json(d, json_file, expand=True, searchString="",replaceString=""):
    data2export = {}
    keys2export = []

    for key in d.keys():
        if key.startswith("_"):
            continue
        elif key.startswith("BB"):
            continue
        elif key.startswith("B_pn"):
            continue
        elif key.startswith("do_"):
            continue
        elif d.getVarFlag(key, "func"):
            continue

        keys2export.append(key)

    for key in keys2export:
        try:
            data2export[key] = d.getVar(key, expand).replace(searchString,replaceString)
        except bb.data_smart.ExpansionError:
            data2export[key] = ''
        except AttributeError:
            pass

    with open(json_file, "w") as f:
        json.dump(data2export, f, skipkeys=True, indent=4, sort_keys=True)
