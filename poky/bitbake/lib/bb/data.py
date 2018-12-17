# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Data' implementations

Functions for interacting with the data structure used by the
BitBake build tools.

The expandKeys and update_data are the most expensive
operations. At night the cookie monster came by and
suggested 'give me cookies on setting the variables and
things will work out'. Taking this suggestion into account
applying the skills from the not yet passed 'Entwurf und
Analyse von Algorithmen' lecture and the cookie
monster seems to be right. We will track setVar more carefully
to have faster update_data and expandKeys operations.

This is a trade-off between speed and memory again but
the speed is more critical here.
"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2005        Holger Hans Peter Freyther
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import sys, os, re
import hashlib
if sys.argv[0][-5:] == "pydoc":
    path = os.path.dirname(os.path.dirname(sys.argv[1]))
else:
    path = os.path.dirname(os.path.dirname(sys.argv[0]))
sys.path.insert(0, path)
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


__expand_var_regexp__ = re.compile(r"\${[^{}]+}")
__expand_python_regexp__ = re.compile(r"\${@.+?}")

def expand(s, d, varname = None):
    """Variable expansion using the data store"""
    return d.expand(s, varname)

def expandKeys(alterdata, readdata = None):
    if readdata == None:
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

    export = d.getVarFlag(var, "export", False)
    unexport = d.getVarFlag(var, "unexport", False)
    if not all and not export and not unexport and not func:
        return False

    try:
        if all:
            oval = d.getVar(var, False)
        val = d.getVar(var)
    except (KeyboardInterrupt, bb.build.FuncFailed):
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
                                      d.getVarFlag(key, 'export', False) and
                                      not d.getVarFlag(key, 'unexport', False))

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
        for dep in deps:
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

def update_data(d):
    """Performs final steps upon the datastore, including application of overrides"""
    d.finalize(parent = True)

def build_dependencies(key, keys, shelldeps, varflagsexcl, d):
    deps = set()
    try:
        if key[-1] == ']':
            vf = key[:-1].split('[')
            value, parser = d.getVarFlag(vf[0], vf[1], False, retparser=True)
            deps |= parser.references
            deps = deps | (keys & parser.execs)
            return deps, value
        varflags = d.getVarFlags(key, ["vardeps", "vardepvalue", "vardepsexclude", "exports", "postfuncs", "prefuncs", "lineno", "filename"]) or {}
        vardeps = varflags.get("vardeps")

        def handle_contains(value, contains, d):
            newvalue = ""
            for k in sorted(contains):
                l = (d.getVar(k) or "").split()
                for item in sorted(contains[k]):
                    for word in item.split():
                        if not word in l:
                            newvalue += "\n%s{%s} = Unset" % (k, item)
                            break
                    else:
                        newvalue += "\n%s{%s} = Set" % (k, item)
            if not newvalue:
                return value
            if not value:
                return newvalue
            return value + newvalue

        def handle_remove(value, deps, removes, d):
            for r in sorted(removes):
                r2 = d.expandWithRefs(r, None)
                value += "\n_remove of %s" % r
                deps |= r2.references
                deps = deps | (keys & r2.execs)
            return value

        if "vardepvalue" in varflags:
            value = varflags.get("vardepvalue")
        elif varflags.get("func"):
            if varflags.get("python"):
                value = d.getVarFlag(key, "_content", False)
                parser = bb.codeparser.PythonParser(key, logger)
                if value and "\t" in value:
                    logger.warning("Variable %s contains tabs, please remove these (%s)" % (key, d.getVar("FILE")))
                parser.parse_python(value, filename=varflags.get("filename"), lineno=varflags.get("lineno"))
                deps = deps | parser.references
                deps = deps | (keys & parser.execs)
                value = handle_contains(value, parser.contains, d)
            else:
                value, parsedvar = d.getVarFlag(key, "_content", False, retparser=True)
                parser = bb.codeparser.ShellParser(key, logger)
                parser.parse_shell(parsedvar.value)
                deps = deps | shelldeps
                deps = deps | parsedvar.references
                deps = deps | (keys & parser.execs) | (keys & parsedvar.execs)
                value = handle_contains(value, parsedvar.contains, d)
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
            value = handle_contains(value, parser.contains, d)
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
        deps -= set(varflags.get("vardepsexclude", "").split())
    except bb.parse.SkipRecipe:
        raise
    except Exception as e:
        bb.warn("Exception during build_dependencies for %s" % key)
        raise
    return deps, value
    #bb.note("Variable %s references %s and calls %s" % (key, str(deps), str(execs)))
    #d.setVarFlag(key, "vardeps", deps)

def generate_dependencies(d):

    keys = set(key for key in d if not key.startswith("__"))
    shelldeps = set(key for key in d.getVar("__exportlist", False) if d.getVarFlag(key, "export", False) and not d.getVarFlag(key, "unexport", False))
    varflagsexcl = d.getVar('BB_SIGNATURE_EXCLUDE_FLAGS')

    deps = {}
    values = {}

    tasklist = d.getVar('__BBTASKS', False) or []
    for task in tasklist:
        deps[task], values[task] = build_dependencies(task, keys, shelldeps, varflagsexcl, d)
        newdeps = deps[task]
        seen = set()
        while newdeps:
            nextdeps = newdeps
            seen |= nextdeps
            newdeps = set()
            for dep in nextdeps:
                if dep not in deps:
                    deps[dep], values[dep] = build_dependencies(dep, keys, shelldeps, varflagsexcl, d)
                newdeps |=  deps[dep]
            newdeps -= seen
        #print "For %s: %s" % (task, str(deps[task]))
    return tasklist, deps, values

def generate_dependency_hash(tasklist, gendeps, lookupcache, whitelist, fn):
    taskdeps = {}
    basehash = {}

    for task in tasklist:
        data = lookupcache[task]

        if data is None:
            bb.error("Task %s from %s seems to be empty?!" % (task, fn))
            data = ''

        gendeps[task] -= whitelist
        newdeps = gendeps[task]
        seen = set()
        while newdeps:
            nextdeps = newdeps
            seen |= nextdeps
            newdeps = set()
            for dep in nextdeps:
                if dep in whitelist:
                    continue
                gendeps[dep] -= whitelist
                newdeps |= gendeps[dep]
            newdeps -= seen

        alldeps = sorted(seen)
        for dep in alldeps:
            data = data + dep
            var = lookupcache[dep]
            if var is not None:
                data = data + str(var)
        k = fn + "." + task
        basehash[k] = hashlib.md5(data.encode("utf-8")).hexdigest()
        taskdeps[task] = alldeps

    return taskdeps, basehash

def inherits_class(klass, d):
    val = d.getVar('__inherit_cache', False) or []
    needle = os.path.join('classes', '%s.bbclass' % klass)
    for v in val:
        if v.endswith(needle):
            return True
    return False
