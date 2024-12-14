"""
BitBake 'Data' implementations

Functions for interacting with the data structure used by the
BitBake build tools.

expandKeys and datastore iteration are the most expensive
operations. Updating overrides is now "on the fly" but still based
on the idea of the cookie monster introduced by zecke:
"At night the cookie monster came by and
suggested 'give me cookies on setting the variables and
things will work out'. Taking this suggestion into account
applying the skills from the not yet passed 'Entwurf und
Analyse von Algorithmen' lecture and the cookie
monster seems to be right. We will track setVar more carefully
to have faster datastore operations."

This is a trade-off between speed and memory again but
the speed is more critical here.
"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2005        Holger Hans Peter Freyther
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import sys, os, re
import hashlib
from itertools import groupby

from bb import data_smart
from bb import codeparser
import bb

logger = data_smart.logger
_dict_type = data_smart.DataSmart

def init():
    """Return a new object representing the Bitbake data"""
    return _dict_type()

def init_db(parent = None):
    """Return a new object representing the Bitbake data,
    optionally based on an existing object"""
    if parent is not None:
        return parent.createCopy()
    else:
        return _dict_type()

def createCopy(source):
    """Link the source set to the destination
    If one does not find the value in the destination set,
    search will go on to the source set to get the value.
    Value from source are copy-on-write. i.e. any try to
    modify one of them will end up putting the modified value
    in the destination set.
    """
    return source.createCopy()

def initVar(var, d):
    """Non-destructive var init for data structure"""
    d.initVar(var)

def keys(d):
    """Return a list of keys in d"""
    return d.keys()

def expand(s, d, varname = None):
    """Variable expansion using the data store"""
    return d.expand(s, varname)

def expandKeys(alterdata, readdata = None):
    if readdata is None:
        readdata = alterdata

    todolist = {}
    for key in alterdata:
        if not '${' in key:
            continue

        ekey = expand(key, readdata)
        if key == ekey:
            continue
        todolist[key] = ekey

    # These two for loops are split for performance to maximise the
    # usefulness of the expand cache
    for key in sorted(todolist):
        ekey = todolist[key]
        newval = alterdata.getVar(ekey, False)
        if newval is not None:
            val = alterdata.getVar(key, False)
            if val is not None:
                bb.warn("Variable key %s (%s) replaces original key %s (%s)." % (key, val, ekey, newval))
        alterdata.renameVar(key, ekey)

def inheritFromOS(d, savedenv, permitted):
    """Inherit variables from the initial environment."""
    exportlist = bb.utils.preserved_envvars_exported()
    for s in savedenv.keys():
        if s in permitted:
            try:
                d.setVar(s, savedenv.getVar(s), op = 'from env')
                if s in exportlist:
                    d.setVarFlag(s, "export", True, op = 'auto env export')
            except TypeError:
                pass

def emit_var(var, o=sys.__stdout__, d = init(), all=False):
    """Emit a variable to be sourced by a shell."""
    func = d.getVarFlag(var, "func", False)
    if d.getVarFlag(var, 'python', False) and func:
        return False

    export = bb.utils.to_boolean(d.getVarFlag(var, "export"))
    unexport = bb.utils.to_boolean(d.getVarFlag(var, "unexport"))
    if not all and not export and not unexport and not func:
        return False

    try:
        if all:
            oval = d.getVar(var, False)
        val = d.getVar(var)
    except (KeyboardInterrupt):
        raise
    except Exception as exc:
        o.write('# expansion of %s threw %s: %s\n' % (var, exc.__class__.__name__, str(exc)))
        return False

    if all:
        d.varhistory.emit(var, oval, val, o, d)

    if (var.find("-") != -1 or var.find(".") != -1 or var.find('{') != -1 or var.find('}') != -1 or var.find('+') != -1) and not all:
        return False

    varExpanded = d.expand(var)

    if unexport:
        o.write('unset %s\n' % varExpanded)
        return False

    if val is None:
        return False

    val = str(val)

    if varExpanded.startswith("BASH_FUNC_"):
        varExpanded = varExpanded[10:-2]
        val = val[3:] # Strip off "() "
        o.write("%s() %s\n" % (varExpanded, val))
        o.write("export -f %s\n" % (varExpanded))
        return True

    if func:
        # Write a comment indicating where the shell function came from (line number and filename) to make it easier
        # for the user to diagnose task failures. This comment is also used by build.py to determine the metadata
        # location of shell functions.
        o.write("# line: {0}, file: {1}\n".format(
            d.getVarFlag(var, "lineno", False),
            d.getVarFlag(var, "filename", False)))
        # NOTE: should probably check for unbalanced {} within the var
        val = val.rstrip('\n')
        o.write("%s() {\n%s\n}\n" % (varExpanded, val))
        return 1

    if export:
        o.write('export ')

    # if we're going to output this within doublequotes,
    # to a shell, we need to escape the quotes in the var
    alter = re.sub('"', '\\"', val)
    alter = re.sub('\n', ' \\\n', alter)
    alter = re.sub('\\$', '\\\\$', alter)
    o.write('%s="%s"\n' % (varExpanded, alter))
    return False

def emit_env(o=sys.__stdout__, d = init(), all=False):
    """Emits all items in the data store in a format such that it can be sourced by a shell."""

    isfunc = lambda key: bool(d.getVarFlag(key, "func", False))
    keys = sorted((key for key in d.keys() if not key.startswith("__")), key=isfunc)
    grouped = groupby(keys, isfunc)
    for isfunc, keys in grouped:
        for key in sorted(keys):
            emit_var(key, o, d, all and not isfunc) and o.write('\n')

def exported_keys(d):
    return (key for key in d.keys() if not key.startswith('__') and
                                      bb.utils.to_boolean(d.getVarFlag(key, 'export')) and
                                      not bb.utils.to_boolean(d.getVarFlag(key, 'unexport')))

def exported_vars(d):
    k = list(exported_keys(d))
    for key in k:
        try:
            value = d.getVar(key)
        except Exception as err:
            bb.warn("%s: Unable to export ${%s}: %s" % (d.getVar("FILE"), key, err))
            continue

        if value is not None:
            yield key, str(value)

def emit_func(func, o=sys.__stdout__, d = init()):
    """Emits all items in the data store in a format such that it can be sourced by a shell."""

    keys = (key for key in d.keys() if not key.startswith("__") and not d.getVarFlag(key, "func", False))
    for key in sorted(keys):
        emit_var(key, o, d, False)

    o.write('\n')
    emit_var(func, o, d, False) and o.write('\n')
    newdeps = bb.codeparser.ShellParser(func, logger).parse_shell(d.getVar(func))
    newdeps |= set((d.getVarFlag(func, "vardeps") or "").split())
    seen = set()
    while newdeps:
        deps = newdeps
        seen |= deps
        newdeps = set()
        for dep in sorted(deps):
            if d.getVarFlag(dep, "func", False) and not d.getVarFlag(dep, "python", False):
               emit_var(dep, o, d, False) and o.write('\n')
               newdeps |=  bb.codeparser.ShellParser(dep, logger).parse_shell(d.getVar(dep))
               newdeps |= set((d.getVarFlag(dep, "vardeps") or "").split())
        newdeps -= seen

_functionfmt = """
def {function}(d):
{body}"""

def emit_func_python(func, o=sys.__stdout__, d = init()):
    """Emits all items in the data store in a format such that it can be sourced by a shell."""

    def write_func(func, o, call = False):
        body = d.getVar(func, False)
        if not body.startswith("def"):
            body = _functionfmt.format(function=func, body=body)

        o.write(body.strip() + "\n\n")
        if call:
            o.write(func + "(d)" + "\n\n")

    write_func(func, o, True)
    pp = bb.codeparser.PythonParser(func, logger)
    pp.parse_python(d.getVar(func, False))
    newdeps = pp.execs
    newdeps |= set((d.getVarFlag(func, "vardeps") or "").split())
    seen = set()
    while newdeps:
        deps = newdeps
        seen |= deps
        newdeps = set()
        for dep in deps:
            if d.getVarFlag(dep, "func", False) and d.getVarFlag(dep, "python", False):
               write_func(dep, o)
               pp = bb.codeparser.PythonParser(dep, logger)
               pp.parse_python(d.getVar(dep, False))
               newdeps |= pp.execs
               newdeps |= set((d.getVarFlag(dep, "vardeps") or "").split())
        newdeps -= seen

def build_dependencies(key, keys, mod_funcs, shelldeps, varflagsexcl, ignored_vars, d, codeparsedata):
    def handle_contains(value, contains, exclusions, d):
        newvalue = []
        if value:
            newvalue.append(str(value))
        for k in sorted(contains):
            if k in exclusions or k in ignored_vars:
                continue
            l = (d.getVar(k) or "").split()
            for item in sorted(contains[k]):
                for word in item.split():
                    if not word in l:
                        newvalue.append("\n%s{%s} = Unset" % (k, item))
                        break
                else:
                    newvalue.append("\n%s{%s} = Set" % (k, item))
        return "".join(newvalue)

    def handle_remove(value, deps, removes, d):
        for r in sorted(removes):
            r2 = d.expandWithRefs(r, None)
            value += "\n_remove of %s" % r
            deps |= r2.references
            deps = deps | (keys & r2.execs)
            value = handle_contains(value, r2.contains, exclusions, d)
        return value

    deps = set()
    try:
        if key in mod_funcs:
            exclusions = set()
            moddep = bb.codeparser.modulecode_deps[key]
            value = handle_contains(moddep[4], moddep[3], exclusions, d)
            return frozenset((moddep[0] | keys & moddep[1]) - ignored_vars), value

        if key[-1] == ']':
            vf = key[:-1].split('[')
            if vf[1] == "vardepvalueexclude":
                return deps, ""
            value, parser = d.getVarFlag(vf[0], vf[1], False, retparser=True)
            deps |= parser.references
            deps = deps | (keys & parser.execs)
            deps -= ignored_vars
            return frozenset(deps), value
        varflags = d.getVarFlags(key, ["vardeps", "vardepvalue", "vardepsexclude", "exports", "postfuncs", "prefuncs", "lineno", "filename"]) or {}
        vardeps = varflags.get("vardeps")
        exclusions = varflags.get("vardepsexclude", "").split()

        if "vardepvalue" in varflags:
            value = varflags.get("vardepvalue")
        elif varflags.get("func"):
            if varflags.get("python"):
                value = codeparsedata.getVarFlag(key, "_content", False)
                parser = bb.codeparser.PythonParser(key, logger)
                parser.parse_python(value, filename=varflags.get("filename"), lineno=varflags.get("lineno"))
                deps = deps | parser.references
                deps = deps | (keys & parser.execs)
                value = handle_contains(value, parser.contains, exclusions, d)
            else:
                value, parsedvar = codeparsedata.getVarFlag(key, "_content", False, retparser=True)
                parser = bb.codeparser.ShellParser(key, logger)
                parser.parse_shell(parsedvar.value)
                deps = deps | shelldeps
                deps = deps | parsedvar.references
                deps = deps | (keys & parser.execs) | (keys & parsedvar.execs)
                value = handle_contains(value, parsedvar.contains, exclusions, d)
                if hasattr(parsedvar, "removes"):
                    value = handle_remove(value, deps, parsedvar.removes, d)
            if vardeps is None:
                parser.log.flush()
            if "prefuncs" in varflags:
                deps = deps | set(varflags["prefuncs"].split())
            if "postfuncs" in varflags:
                deps = deps | set(varflags["postfuncs"].split())
            if "exports" in varflags:
                deps = deps | set(varflags["exports"].split())
        else:
            value, parser = d.getVarFlag(key, "_content", False, retparser=True)
            deps |= parser.references
            deps = deps | (keys & parser.execs)
            value = handle_contains(value, parser.contains, exclusions, d)
            if hasattr(parser, "removes"):
                value = handle_remove(value, deps, parser.removes, d)

        if "vardepvalueexclude" in varflags:
            exclude = varflags.get("vardepvalueexclude")
            for excl in exclude.split('|'):
                if excl:
                    value = value.replace(excl, '')

        # Add varflags, assuming an exclusion list is set
        if varflagsexcl:
            varfdeps = []
            for f in varflags:
                if f not in varflagsexcl:
                    varfdeps.append('%s[%s]' % (key, f))
            if varfdeps:
                deps |= set(varfdeps)

        deps |= set((vardeps or "").split())
        deps -= set(exclusions)
        deps -= ignored_vars
    except bb.parse.SkipRecipe:
        raise
    except Exception as e:
        bb.warn("Exception during build_dependencies for %s" % key)
        raise
    return frozenset(deps), value
    #bb.note("Variable %s references %s and calls %s" % (key, str(deps), str(execs)))
    #d.setVarFlag(key, "vardeps", deps)

def generate_dependencies(d, ignored_vars):

    mod_funcs = set(bb.codeparser.modulecode_deps.keys())
    keys = set(key for key in d if not key.startswith("__")) | mod_funcs
    shelldeps = set(key for key in d.getVar("__exportlist", False) if bb.utils.to_boolean(d.getVarFlag(key, "export")) and not bb.utils.to_boolean(d.getVarFlag(key, "unexport")))
    varflagsexcl = d.getVar('BB_SIGNATURE_EXCLUDE_FLAGS')

    codeparserd = d.createCopy()
    for forced in (d.getVar('BB_HASH_CODEPARSER_VALS') or "").split():
        key, value = forced.split("=", 1)
        codeparserd.setVar(key, value)

    deps = {}
    values = {}

    tasklist = d.getVar('__BBTASKS', False) or []
    for task in tasklist:
        deps[task], values[task] = build_dependencies(task, keys, mod_funcs, shelldeps, varflagsexcl, ignored_vars, d, codeparserd)
        newdeps = deps[task]
        seen = set()
        while newdeps:
            nextdeps = newdeps
            seen |= nextdeps
            newdeps = set()
            for dep in nextdeps:
                if dep not in deps:
                    deps[dep], values[dep] = build_dependencies(dep, keys, mod_funcs, shelldeps, varflagsexcl, ignored_vars, d, codeparserd)
                newdeps |=  deps[dep]
            newdeps -= seen
        #print "For %s: %s" % (task, str(deps[task]))
    return tasklist, deps, values

def generate_dependency_hash(tasklist, gendeps, lookupcache, ignored_vars, fn):
    taskdeps = {}
    basehash = {}

    for task in tasklist:
        data = lookupcache[task]

        if data is None:
            bb.error("Task %s from %s seems to be empty?!" % (task, fn))
            data = []
        else:
            data = [data]

        newdeps = gendeps[task]
        seen = set()
        while newdeps:
            nextdeps = newdeps
            seen |= nextdeps
            newdeps = set()
            for dep in nextdeps:
                newdeps |= gendeps[dep]
            newdeps -= seen

        alldeps = sorted(seen)
        for dep in alldeps:
            data.append(dep)
            var = lookupcache[dep]
            if var is not None:
                data.append(str(var))
        k = fn + ":" + task
        basehash[k] = hashlib.sha256("".join(data).encode("utf-8")).hexdigest()
        taskdeps[task] = frozenset(seen)

    return taskdeps, basehash

def inherits_class(klass, d):
    val = d.getVar('__inherit_cache', False) or []
    needle = '/%s.bbclass' % klass
    for v in val:
        if v.endswith(needle):
            return True
    return False
