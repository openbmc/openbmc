"""
BitBake Smart Dictionary Implementation

Functions for interacting with the data structure used by the
BitBake build tools.

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2004, 2005  Seb Frankengul
# Copyright (C) 2005, 2006  Holger Hans Peter Freyther
# Copyright (C) 2005        Uli Luckas
# Copyright (C) 2005        ROAD GmbH
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import copy, re, sys, traceback
from collections import MutableMapping
import logging
import hashlib
import bb, bb.codeparser
from bb   import utils
from bb.COW  import COWDictBase

logger = logging.getLogger("BitBake.Data")

__setvar_keyword__ = ["_append", "_prepend", "_remove"]
__setvar_regexp__ = re.compile(r'(?P<base>.*?)(?P<keyword>_append|_prepend|_remove)(_(?P<add>[^A-Z]*))?$')
__expand_var_regexp__ = re.compile(r"\${[a-zA-Z0-9\-_+./~]+?}")
__expand_python_regexp__ = re.compile(r"\${@.+?}")
__whitespace_split__ = re.compile(r'(\s)')
__override_regexp__ = re.compile(r'[a-z0-9]+')

def infer_caller_details(loginfo, parent = False, varval = True):
    """Save the caller the trouble of specifying everything."""
    # Save effort.
    if 'ignore' in loginfo and loginfo['ignore']:
        return
    # If nothing was provided, mark this as possibly unneeded.
    if not loginfo:
        loginfo['ignore'] = True
        return
    # Infer caller's likely values for variable (var) and value (value), 
    # to reduce clutter in the rest of the code.
    above = None
    def set_above():
        try:
            raise Exception
        except Exception:
            tb = sys.exc_info()[2]
            if parent:
                return tb.tb_frame.f_back.f_back.f_back
            else:
                return tb.tb_frame.f_back.f_back

    if varval and ('variable' not in loginfo or 'detail' not in loginfo):
        if not above:
            above = set_above()
        lcls = above.f_locals.items()
        for k, v in lcls:
            if k == 'value' and 'detail' not in loginfo:
                loginfo['detail'] = v
            if k == 'var' and 'variable' not in loginfo:
                loginfo['variable'] = v
    # Infer file/line/function from traceback
    # Don't use traceback.extract_stack() since it fills the line contents which
    # we don't need and that hits stat syscalls
    if 'file' not in loginfo:
        if not above:
            above = set_above()
        f = above.f_back
        line = f.f_lineno
        file = f.f_code.co_filename
        func = f.f_code.co_name
        loginfo['file'] = file
        loginfo['line'] = line
        if func not in loginfo:
            loginfo['func'] = func

class VariableParse:
    def __init__(self, varname, d, val = None):
        self.varname = varname
        self.d = d
        self.value = val

        self.references = set()
        self.execs = set()
        self.contains = {}

    def var_sub(self, match):
            key = match.group()[2:-1]
            if self.varname and key:
                if self.varname == key:
                    raise Exception("variable %s references itself!" % self.varname)
            var = self.d.getVarFlag(key, "_content")
            self.references.add(key)
            if var is not None:
                return var
            else:
                return match.group()

    def python_sub(self, match):
            if isinstance(match, str):
                code = match
            else:
                code = match.group()[3:-1]

            if self.varname:
                varname = 'Var <%s>' % self.varname
            else:
                varname = '<expansion>'
            codeobj = compile(code.strip(), varname, "eval")

            parser = bb.codeparser.PythonParser(self.varname, logger)
            parser.parse_python(code)
            if self.varname:
                vardeps = self.d.getVarFlag(self.varname, "vardeps")
                if vardeps is None:
                    parser.log.flush()
            else:
                parser.log.flush()
            self.references |= parser.references
            self.execs |= parser.execs

            for k in parser.contains:
                if k not in self.contains:
                    self.contains[k] = parser.contains[k].copy()
                else:
                    self.contains[k].update(parser.contains[k])
            value = utils.better_eval(codeobj, DataContext(self.d), {'d' : self.d})
            return str(value)


class DataContext(dict):
    def __init__(self, metadata, **kwargs):
        self.metadata = metadata
        dict.__init__(self, **kwargs)
        self['d'] = metadata

    def __missing__(self, key):
        value = self.metadata.getVar(key)
        if value is None or self.metadata.getVarFlag(key, 'func', False):
            raise KeyError(key)
        else:
            return value

class ExpansionError(Exception):
    def __init__(self, varname, expression, exception):
        self.expression = expression
        self.variablename = varname
        self.exception = exception
        if varname:
            if expression:
                self.msg = "Failure expanding variable %s, expression was %s which triggered exception %s: %s" % (varname, expression, type(exception).__name__, exception)
            else:
                self.msg = "Failure expanding variable %s: %s: %s" % (varname, type(exception).__name__, exception)
        else:
            self.msg = "Failure expanding expression %s which triggered exception %s: %s" % (expression, type(exception).__name__, exception)
        Exception.__init__(self, self.msg)
        self.args = (varname, expression, exception)
    def __str__(self):
        return self.msg

class IncludeHistory(object):
    def __init__(self, parent = None, filename = '[TOP LEVEL]'):
        self.parent = parent
        self.filename = filename
        self.children = []
        self.current = self

    def copy(self):
        new = IncludeHistory(self.parent, self.filename)
        for c in self.children:
            new.children.append(c)
        return new

    def include(self, filename):
        newfile = IncludeHistory(self.current, filename)
        self.current.children.append(newfile)
        self.current = newfile
        return self

    def __enter__(self):
        pass

    def __exit__(self, a, b, c):
        if self.current.parent:
            self.current = self.current.parent
        else:
            bb.warn("Include log: Tried to finish '%s' at top level." % filename)
        return False

    def emit(self, o, level = 0):
        """Emit an include history file, and its children."""
        if level:
            spaces = "  " * (level - 1)
            o.write("# %s%s" % (spaces, self.filename))
            if len(self.children) > 0:
                o.write(" includes:")
        else:
            o.write("#\n# INCLUDE HISTORY:\n#")
        level = level + 1
        for child in self.children:
            o.write("\n")
            child.emit(o, level)

class VariableHistory(object):
    def __init__(self, dataroot):
        self.dataroot = dataroot
        self.variables = COWDictBase.copy()

    def copy(self):
        new = VariableHistory(self.dataroot)
        new.variables = self.variables.copy()
        return new

    def __getstate__(self):
        vardict = {}
        for k, v in self.variables.iteritems():
            vardict[k] = v
        return {'dataroot': self.dataroot,
                'variables': vardict}

    def __setstate__(self, state):
        self.dataroot = state['dataroot']
        self.variables = COWDictBase.copy()
        for k, v in state['variables'].items():
            self.variables[k] = v

    def record(self, *kwonly, **loginfo):
        if not self.dataroot._tracking:
            return
        if len(kwonly) > 0:
            raise TypeError
        infer_caller_details(loginfo, parent = True)
        if 'ignore' in loginfo and loginfo['ignore']:
            return
        if 'op' not in loginfo or not loginfo['op']:
            loginfo['op'] = 'set'
        if 'detail' in loginfo:
            loginfo['detail'] = str(loginfo['detail'])
        if 'variable' not in loginfo or 'file' not in loginfo:
            raise ValueError("record() missing variable or file.")
        var = loginfo['variable']

        if var not in self.variables:
            self.variables[var] = []
        if not isinstance(self.variables[var], list):
            return
        if 'nodups' in loginfo and loginfo in self.variables[var]:
            return
        self.variables[var].append(loginfo.copy())

    def rename_variable_hist(self, oldvar, newvar):
        if not self.dataroot._tracking:
            return
        if oldvar not in self.variables:
            return
        if newvar not in self.variables:
            self.variables[newvar] = []
        for i in self.variables[oldvar]:
            self.variables[newvar].append(i.copy())

    def variable(self, var):
        varhistory = []
        if var in self.variables:
            varhistory.extend(self.variables[var])
        return varhistory

    def emit(self, var, oval, val, o, d):
        history = self.variable(var)

        # Append override history
        if var in d.overridedata:
            for (r, override) in d.overridedata[var]:
                for event in self.variable(r):
                    loginfo = event.copy()
                    if 'flag' in loginfo and not loginfo['flag'].startswith("_"):
                        continue
                    loginfo['variable'] = var
                    loginfo['op'] = 'override[%s]:%s' % (override, loginfo['op'])
                    history.append(loginfo)

        commentVal = re.sub('\n', '\n#', str(oval))
        if history:
            if len(history) == 1:
                o.write("#\n# $%s\n" % var)
            else:
                o.write("#\n# $%s [%d operations]\n" % (var, len(history)))
            for event in history:
                # o.write("# %s\n" % str(event))
                if 'func' in event:
                    # If we have a function listed, this is internal
                    # code, not an operation in a config file, and the
                    # full path is distracting.
                    event['file'] = re.sub('.*/', '', event['file'])
                    display_func = ' [%s]' % event['func']
                else:
                    display_func = ''
                if 'flag' in event:
                    flag = '[%s] ' % (event['flag'])
                else:
                    flag = ''
                o.write("#   %s %s:%s%s\n#     %s\"%s\"\n" % (event['op'], event['file'], event['line'], display_func, flag, re.sub('\n', '\n#     ', event['detail'])))
            if len(history) > 1:
                o.write("# pre-expansion value:\n")
                o.write('#   "%s"\n' % (commentVal))
        else:
            o.write("#\n# $%s\n#   [no history recorded]\n#\n" % var)
            o.write('#   "%s"\n' % (commentVal))

    def get_variable_files(self, var):
        """Get the files where operations are made on a variable"""
        var_history = self.variable(var)
        files = []
        for event in var_history:
            files.append(event['file'])
        return files

    def get_variable_lines(self, var, f):
        """Get the line where a operation is made on a variable in file f"""
        var_history = self.variable(var)
        lines = []
        for event in var_history:
            if f== event['file']:
                line = event['line']
                lines.append(line)
        return lines

    def get_variable_items_files(self, var):
        """
        Use variable history to map items added to a list variable and
        the files in which they were added.
        """
        d = self.dataroot
        history = self.variable(var)
        finalitems = (d.getVar(var) or '').split()
        filemap = {}
        isset = False
        for event in history:
            if 'flag' in event:
                continue
            if event['op'] == '_remove':
                continue
            if isset and event['op'] == 'set?':
                continue
            isset = True
            items = d.expand(event['detail']).split()
            for item in items:
                # This is a little crude but is belt-and-braces to avoid us
                # having to handle every possible operation type specifically
                if item in finalitems and not item in filemap:
                    filemap[item] = event['file']
        return filemap

    def del_var_history(self, var, f=None, line=None):
        """If file f and line are not given, the entire history of var is deleted"""
        if var in self.variables:
            if f and line:
                self.variables[var] = [ x for x in self.variables[var] if x['file']!=f and x['line']!=line]
            else:
                self.variables[var] = []

class DataSmart(MutableMapping):
    def __init__(self):
        self.dict = {}

        self.inchistory = IncludeHistory()
        self.varhistory = VariableHistory(self)
        self._tracking = False

        self.expand_cache = {}

        # cookie monster tribute
        # Need to be careful about writes to overridedata as
        # its only a shallow copy, could influence other data store
        # copies!
        self.overridedata = {}
        self.overrides = None
        self.overridevars = set(["OVERRIDES", "FILE"])
        self.inoverride = False

    def enableTracking(self):
        self._tracking = True

    def disableTracking(self):
        self._tracking = False

    def expandWithRefs(self, s, varname):

        if not isinstance(s, str): # sanity check
            return VariableParse(varname, self, s)

        varparse = VariableParse(varname, self)

        while s.find('${') != -1:
            olds = s
            try:
                s = __expand_var_regexp__.sub(varparse.var_sub, s)
                try:
                    s = __expand_python_regexp__.sub(varparse.python_sub, s)
                except SyntaxError as e:
                    # Likely unmatched brackets, just don't expand the expression
                    if e.msg != "EOL while scanning string literal":
                        raise
                if s == olds:
                    break
            except ExpansionError:
                raise
            except bb.parse.SkipRecipe:
                raise
            except Exception as exc:
                tb = sys.exc_info()[2]
                raise ExpansionError(varname, s, exc).with_traceback(tb) from exc

        varparse.value = s

        return varparse

    def expand(self, s, varname = None):
        return self.expandWithRefs(s, varname).value

    def finalize(self, parent = False):
        return

    def internal_finalize(self, parent = False):
        """Performs final steps upon the datastore, including application of overrides"""
        self.overrides = None

    def need_overrides(self):
        if self.overrides is not None:
            return
        if self.inoverride:
            return
        for count in range(5):
            self.inoverride = True
            # Can end up here recursively so setup dummy values
            self.overrides = []
            self.overridesset = set()
            self.overrides = (self.getVar("OVERRIDES") or "").split(":") or []
            self.overridesset = set(self.overrides)
            self.inoverride = False
            self.expand_cache = {}
            newoverrides = (self.getVar("OVERRIDES") or "").split(":") or []
            if newoverrides == self.overrides:
                break
            self.overrides = newoverrides
            self.overridesset = set(self.overrides)
        else:
            bb.fatal("Overrides could not be expanded into a stable state after 5 iterations, overrides must be being referenced by other overridden variables in some recursive fashion. Please provide your configuration to bitbake-devel so we can laugh, er, I mean try and understand how to make it work.")

    def initVar(self, var):
        self.expand_cache = {}
        if not var in self.dict:
            self.dict[var] = {}

    def _findVar(self, var):
        dest = self.dict
        while dest:
            if var in dest:
                return dest[var], self.overridedata.get(var, None)

            if "_data" not in dest:
                break
            dest = dest["_data"]
        return None, self.overridedata.get(var, None)

    def _makeShadowCopy(self, var):
        if var in self.dict:
            return

        local_var, _ = self._findVar(var)

        if local_var:
            self.dict[var] = copy.copy(local_var)
        else:
            self.initVar(var)


    def setVar(self, var, value, **loginfo):
        #print("var=" + str(var) + "  val=" + str(value))
        self.expand_cache = {}
        parsing=False
        if 'parsing' in loginfo:
            parsing=True

        if 'op' not in loginfo:
            loginfo['op'] = "set"

        match  = __setvar_regexp__.match(var)
        if match and match.group("keyword") in __setvar_keyword__:
            base = match.group('base')
            keyword = match.group("keyword")
            override = match.group('add')
            l = self.getVarFlag(base, keyword, False) or []
            l.append([value, override])
            self.setVarFlag(base, keyword, l, ignore=True)
            # And cause that to be recorded:
            loginfo['detail'] = value
            loginfo['variable'] = base
            if override:
                loginfo['op'] = '%s[%s]' % (keyword, override)
            else:
                loginfo['op'] = keyword
            self.varhistory.record(**loginfo)
            # todo make sure keyword is not __doc__ or __module__
            # pay the cookie monster

            # more cookies for the cookie monster
            if '_' in var:
                self._setvar_update_overrides(base, **loginfo)

            if base in self.overridevars:
                self._setvar_update_overridevars(var, value)
            return

        if not var in self.dict:
            self._makeShadowCopy(var)

        if not parsing:
            if "_append" in self.dict[var]:
                del self.dict[var]["_append"]
            if "_prepend" in self.dict[var]:
                del self.dict[var]["_prepend"]
            if "_remove" in self.dict[var]:
                del self.dict[var]["_remove"]
            if var in self.overridedata:
                active = []
                self.need_overrides()
                for (r, o) in self.overridedata[var]:
                    if o in self.overridesset:
                        active.append(r)
                    elif "_" in o:
                        if set(o.split("_")).issubset(self.overridesset):
                            active.append(r)
                for a in active:
                    self.delVar(a)
                del self.overridedata[var]

        # more cookies for the cookie monster
        if '_' in var:
            self._setvar_update_overrides(var, **loginfo)

        # setting var
        self.dict[var]["_content"] = value
        self.varhistory.record(**loginfo)

        if var in self.overridevars:
            self._setvar_update_overridevars(var, value)

    def _setvar_update_overridevars(self, var, value):
        vardata = self.expandWithRefs(value, var)
        new = vardata.references
        new.update(vardata.contains.keys())
        while not new.issubset(self.overridevars):
            nextnew = set()
            self.overridevars.update(new)
            for i in new:
                vardata = self.expandWithRefs(self.getVar(i), i)
                nextnew.update(vardata.references)
                nextnew.update(vardata.contains.keys())
            new = nextnew
        self.internal_finalize(True)

    def _setvar_update_overrides(self, var, **loginfo):
        # aka pay the cookie monster
        override = var[var.rfind('_')+1:]
        shortvar = var[:var.rfind('_')]
        while override and __override_regexp__.match(override):
            if shortvar not in self.overridedata:
                self.overridedata[shortvar] = []
            if [var, override] not in self.overridedata[shortvar]:
                # Force CoW by recreating the list first
                self.overridedata[shortvar] = list(self.overridedata[shortvar])
                self.overridedata[shortvar].append([var, override])
            override = None
            if "_" in shortvar:
                override = var[shortvar.rfind('_')+1:]
                shortvar = var[:shortvar.rfind('_')]
                if len(shortvar) == 0:
                    override = None

    def getVar(self, var, expand=True, noweakdefault=False, parsing=False):
        return self.getVarFlag(var, "_content", expand, noweakdefault, parsing)

    def renameVar(self, key, newkey, **loginfo):
        """
        Rename the variable key to newkey
        """
        if key == newkey:
            bb.warn("Calling renameVar with equivalent keys (%s) is invalid" % key)
            return

        val = self.getVar(key, 0, parsing=True)
        if val is not None:
            self.varhistory.rename_variable_hist(key, newkey)
            loginfo['variable'] = newkey
            loginfo['op'] = 'rename from %s' % key
            loginfo['detail'] = val
            self.varhistory.record(**loginfo)
            self.setVar(newkey, val, ignore=True, parsing=True)

        for i in (__setvar_keyword__):
            src = self.getVarFlag(key, i, False)
            if src is None:
                continue

            dest = self.getVarFlag(newkey, i, False) or []
            dest.extend(src)
            self.setVarFlag(newkey, i, dest, ignore=True)

        if key in self.overridedata:
            self.overridedata[newkey] = []
            for (v, o) in self.overridedata[key]:
                self.overridedata[newkey].append([v.replace(key, newkey), o])
                self.renameVar(v, v.replace(key, newkey))

        if '_' in newkey and val is None:
            self._setvar_update_overrides(newkey, **loginfo)

        loginfo['variable'] = key
        loginfo['op'] = 'rename (to)'
        loginfo['detail'] = newkey
        self.varhistory.record(**loginfo)
        self.delVar(key, ignore=True)

    def appendVar(self, var, value, **loginfo):
        loginfo['op'] = 'append'
        self.varhistory.record(**loginfo)
        self.setVar(var + "_append", value, ignore=True, parsing=True)

    def prependVar(self, var, value, **loginfo):
        loginfo['op'] = 'prepend'
        self.varhistory.record(**loginfo)
        self.setVar(var + "_prepend", value, ignore=True, parsing=True)

    def delVar(self, var, **loginfo):
        self.expand_cache = {}

        loginfo['detail'] = ""
        loginfo['op'] = 'del'
        self.varhistory.record(**loginfo)
        self.dict[var] = {}
        if var in self.overridedata:
            del self.overridedata[var]
        if '_' in var:
            override = var[var.rfind('_')+1:]
            shortvar = var[:var.rfind('_')]
            while override and override.islower():
                try:
                    if shortvar in self.overridedata:
                        # Force CoW by recreating the list first
                        self.overridedata[shortvar] = list(self.overridedata[shortvar])
                        self.overridedata[shortvar].remove([var, override])
                except ValueError as e:
                    pass
                override = None
                if "_" in shortvar:
                    override = var[shortvar.rfind('_')+1:]
                    shortvar = var[:shortvar.rfind('_')]
                    if len(shortvar) == 0:
                         override = None

    def setVarFlag(self, var, flag, value, **loginfo):
        self.expand_cache = {}

        if 'op' not in loginfo:
            loginfo['op'] = "set"
        loginfo['flag'] = flag
        self.varhistory.record(**loginfo)
        if not var in self.dict:
            self._makeShadowCopy(var)
        self.dict[var][flag] = value

        if flag == "_defaultval" and '_' in var:
            self._setvar_update_overrides(var, **loginfo)
        if flag == "_defaultval" and var in self.overridevars:
            self._setvar_update_overridevars(var, value)

        if flag == "unexport" or flag == "export":
            if not "__exportlist" in self.dict:
                self._makeShadowCopy("__exportlist")
            if not "_content" in self.dict["__exportlist"]:
                self.dict["__exportlist"]["_content"] = set()
            self.dict["__exportlist"]["_content"].add(var)

    def getVarFlag(self, var, flag, expand=True, noweakdefault=False, parsing=False, retparser=False):
        if flag == "_content":
            cachename = var
        else:
            if not flag:
                bb.warn("Calling getVarFlag with flag unset is invalid")
                return None
            cachename = var + "[" + flag + "]"

        if expand and cachename in self.expand_cache:
            return self.expand_cache[cachename].value

        local_var, overridedata = self._findVar(var)
        value = None
        removes = set()
        if flag == "_content" and overridedata is not None and not parsing:
            match = False
            active = {}
            self.need_overrides()
            for (r, o) in overridedata:
                # What about double overrides both with "_" in the name?
                if o in self.overridesset:
                    active[o] = r
                elif "_" in o:
                    if set(o.split("_")).issubset(self.overridesset):
                        active[o] = r

            mod = True
            while mod:
                mod = False
                for o in self.overrides:
                    for a in active.copy():
                        if a.endswith("_" + o):
                            t = active[a]
                            del active[a]
                            active[a.replace("_" + o, "")] = t
                            mod = True
                        elif a == o:
                            match = active[a]
                            del active[a]
            if match:
                value, subparser = self.getVarFlag(match, "_content", False, retparser=True)
                if hasattr(subparser, "removes"):
                    # We have to carry the removes from the overridden variable to apply at the
                    # end of processing
                    removes = subparser.removes

        if local_var is not None and value is None:
            if flag in local_var:
                value = copy.copy(local_var[flag])
            elif flag == "_content" and "_defaultval" in local_var and not noweakdefault:
                value = copy.copy(local_var["_defaultval"])


        if flag == "_content" and local_var is not None and "_append" in local_var and not parsing:
            if not value:
                value = ""
            self.need_overrides()
            for (r, o) in local_var["_append"]:
                match = True
                if o:
                    for o2 in o.split("_"):
                        if not o2 in self.overrides:
                            match = False                            
                if match:
                    value = value + r

        if flag == "_content" and local_var is not None and "_prepend" in local_var and not parsing:
            if not value:
                value = ""
            self.need_overrides()
            for (r, o) in local_var["_prepend"]:

                match = True
                if o:
                    for o2 in o.split("_"):
                        if not o2 in self.overrides:
                            match = False                            
                if match:
                    value = r + value

        parser = None
        if expand or retparser:
            parser = self.expandWithRefs(value, cachename)
        if expand:
            value = parser.value

        if value and flag == "_content" and local_var is not None and "_remove" in local_var and not parsing:
            self.need_overrides()
            for (r, o) in local_var["_remove"]:
                match = True
                if o:
                    for o2 in o.split("_"):
                        if not o2 in self.overrides:
                            match = False                            
                if match:
                    removes.add(r)

        if value and flag == "_content" and not parsing:
            if removes and parser:
                expanded_removes = {}
                for r in removes:
                    expanded_removes[r] = self.expand(r).split()

                parser.removes = set()
                val = ""
                for v in __whitespace_split__.split(parser.value):
                    skip = False
                    for r in removes:
                        if v in expanded_removes[r]:
                            parser.removes.add(r)
                            skip = True
                    if skip:
                        continue
                    val = val + v
                parser.value = val
                if expand:
                    value = parser.value

        if parser:
            self.expand_cache[cachename] = parser

        if retparser:
            return value, parser

        return value

    def delVarFlag(self, var, flag, **loginfo):
        self.expand_cache = {}

        local_var, _ = self._findVar(var)
        if not local_var:
            return
        if not var in self.dict:
            self._makeShadowCopy(var)

        if var in self.dict and flag in self.dict[var]:
            loginfo['detail'] = ""
            loginfo['op'] = 'delFlag'
            loginfo['flag'] = flag
            self.varhistory.record(**loginfo)

            del self.dict[var][flag]

    def appendVarFlag(self, var, flag, value, **loginfo):
        loginfo['op'] = 'append'
        loginfo['flag'] = flag
        self.varhistory.record(**loginfo)
        newvalue = (self.getVarFlag(var, flag, False) or "") + value
        self.setVarFlag(var, flag, newvalue, ignore=True)

    def prependVarFlag(self, var, flag, value, **loginfo):
        loginfo['op'] = 'prepend'
        loginfo['flag'] = flag
        self.varhistory.record(**loginfo)
        newvalue = value + (self.getVarFlag(var, flag, False) or "")
        self.setVarFlag(var, flag, newvalue, ignore=True)

    def setVarFlags(self, var, flags, **loginfo):
        self.expand_cache = {}
        infer_caller_details(loginfo)
        if not var in self.dict:
            self._makeShadowCopy(var)

        for i in flags:
            if i == "_content":
                continue
            loginfo['flag'] = i
            loginfo['detail'] = flags[i]
            self.varhistory.record(**loginfo)
            self.dict[var][i] = flags[i]

    def getVarFlags(self, var, expand = False, internalflags=False):
        local_var, _ = self._findVar(var)
        flags = {}

        if local_var:
            for i in local_var:
                if i.startswith("_") and not internalflags:
                    continue
                flags[i] = local_var[i]
                if expand and i in expand:
                    flags[i] = self.expand(flags[i], var + "[" + i + "]")
        if len(flags) == 0:
            return None
        return flags


    def delVarFlags(self, var, **loginfo):
        self.expand_cache = {}
        if not var in self.dict:
            self._makeShadowCopy(var)

        if var in self.dict:
            content = None

            loginfo['op'] = 'delete flags'
            self.varhistory.record(**loginfo)

            # try to save the content
            if "_content" in self.dict[var]:
                content  = self.dict[var]["_content"]
                self.dict[var]            = {}
                self.dict[var]["_content"] = content
            else:
                del self.dict[var]

    def createCopy(self):
        """
        Create a copy of self by setting _data to self
        """
        # we really want this to be a DataSmart...
        data = DataSmart()
        data.dict["_data"] = self.dict
        data.varhistory = self.varhistory.copy()
        data.varhistory.dataroot = data
        data.inchistory = self.inchistory.copy()

        data._tracking = self._tracking

        data.overrides = None
        data.overridevars = copy.copy(self.overridevars)
        # Should really be a deepcopy but has heavy overhead.
        # Instead, we're careful with writes.
        data.overridedata = copy.copy(self.overridedata)

        return data

    def expandVarref(self, variable, parents=False):
        """Find all references to variable in the data and expand it
           in place, optionally descending to parent datastores."""

        if parents:
            keys = iter(self)
        else:
            keys = self.localkeys()

        ref = '${%s}' % variable
        value = self.getVar(variable, False)
        for key in keys:
            referrervalue = self.getVar(key, False)
            if referrervalue and ref in referrervalue:
                self.setVar(key, referrervalue.replace(ref, value))

    def localkeys(self):
        for key in self.dict:
            if key not in ['_data']:
                yield key

    def __iter__(self):
        deleted = set()
        overrides = set()
        def keylist(d):        
            klist = set()
            for key in d:
                if key in ["_data"]:
                    continue
                if key in deleted:
                    continue
                if key in overrides:
                    continue
                if not d[key]:
                    deleted.add(key)
                    continue
                klist.add(key)

            if "_data" in d:
                klist |= keylist(d["_data"])

            return klist

        self.need_overrides()
        for var in self.overridedata:
            for (r, o) in self.overridedata[var]:
                if o in self.overridesset:
                    overrides.add(var)
                elif "_" in o:
                    if set(o.split("_")).issubset(self.overridesset):
                        overrides.add(var)

        for k in keylist(self.dict):
             yield k

        for k in overrides:
             yield k

    def __len__(self):
        return len(frozenset(iter(self)))

    def __getitem__(self, item):
        value = self.getVar(item, False)
        if value is None:
            raise KeyError(item)
        else:
            return value

    def __setitem__(self, var, value):
        self.setVar(var, value)

    def __delitem__(self, var):
        self.delVar(var)

    def get_hash(self):
        data = {}
        d = self.createCopy()
        bb.data.expandKeys(d)

        config_whitelist = set((d.getVar("BB_HASHCONFIG_WHITELIST") or "").split())
        keys = set(key for key in iter(d) if not key.startswith("__"))
        for key in keys:
            if key in config_whitelist:
                continue

            value = d.getVar(key, False) or ""
            if type(value) is type(self):
                data.update({key:value.get_hash()})
            else:
                data.update({key:value})

            varflags = d.getVarFlags(key, internalflags = True, expand=["vardepvalue"])
            if not varflags:
                continue
            for f in varflags:
                if f == "_content":
                    continue
                data.update({'%s[%s]' % (key, f):varflags[f]})

        for key in ["__BBTASKS", "__BBANONFUNCS", "__BBHANDLERS"]:
            bb_list = d.getVar(key, False) or []
            data.update({key:str(bb_list)})

            if key == "__BBANONFUNCS":
                for i in bb_list:
                    value = d.getVar(i, False) or ""
                    data.update({i:value})

        data_str = str([(k, data[k]) for k in sorted(data.keys())])
        return hashlib.sha256(data_str.encode("utf-8")).hexdigest()
