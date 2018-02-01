"""
BitBake code parser

Parses actual code (i.e. python and shell) for functions and in-line
expressions. Used mainly to determine dependencies on other functions
and variables within the BitBake metadata. Also provides a cache for
this information in order to speed up processing.

(Not to be confused with the code that parses the metadata itself,
see lib/bb/parse/ for that).

NOTE: if you change how the parsers gather information you will almost
certainly need to increment CodeParserCache.CACHE_VERSION below so that
any existing codeparser cache gets invalidated. Additionally you'll need
to increment __cache_version__ in cache.py in order to ensure that old
recipe caches don't trigger "Taskhash mismatch" errors.

"""

import ast
import sys
import codegen
import logging
import pickle
import bb.pysh as pysh
import os.path
import bb.utils, bb.data
import hashlib
from itertools import chain
from bb.pysh import pyshyacc, pyshlex, sherrors
from bb.cache import MultiProcessCache

logger = logging.getLogger('BitBake.CodeParser')

def bbhash(s):
    return hashlib.md5(s.encode("utf-8")).hexdigest()

def check_indent(codestr):
    """If the code is indented, add a top level piece of code to 'remove' the indentation"""

    i = 0
    while codestr[i] in ["\n", "\t", " "]:
        i = i + 1

    if i == 0:
        return codestr

    if codestr[i-1] == "\t" or codestr[i-1] == " ":
        if codestr[0] == "\n":
            # Since we're adding a line, we need to remove one line of any empty padding
            # to ensure line numbers are correct
            codestr = codestr[1:]
        return "if 1:\n" + codestr

    return codestr


# Basically pickle, in python 2.7.3 at least, does badly with data duplication 
# upon pickling and unpickling. Combine this with duplicate objects and things
# are a mess.
#
# When the sets are originally created, python calls intern() on the set keys
# which significantly improves memory usage. Sadly the pickle/unpickle process
# doesn't call intern() on the keys and results in the same strings being duplicated
# in memory. This also means pickle will save the same string multiple times in
# the cache file.
#
# By having shell and python cacheline objects with setstate/getstate, we force
# the object creation through our own routine where we can call intern (via internSet).
#
# We also use hashable frozensets and ensure we use references to these so that
# duplicates can be removed, both in memory and in the resulting pickled data.
#
# By playing these games, the size of the cache file shrinks dramatically
# meaning faster load times and the reloaded cache files also consume much less
# memory. Smaller cache files, faster load times and lower memory usage is good.
#
# A custom getstate/setstate using tuples is actually worth 15% cachesize by
# avoiding duplication of the attribute names!

class SetCache(object):
    def __init__(self):
        self.setcache = {}

    def internSet(self, items):
        
        new = []
        for i in items:
            new.append(sys.intern(i))
        s = frozenset(new)
        h = hash(s)
        if h in self.setcache:
            return self.setcache[h]
        self.setcache[h] = s
        return s

codecache = SetCache()

class pythonCacheLine(object):
    def __init__(self, refs, execs, contains):
        self.refs = codecache.internSet(refs)
        self.execs = codecache.internSet(execs)
        self.contains = {}
        for c in contains:
            self.contains[c] = codecache.internSet(contains[c])

    def __getstate__(self):
        return (self.refs, self.execs, self.contains)

    def __setstate__(self, state):
        (refs, execs, contains) = state
        self.__init__(refs, execs, contains)
    def __hash__(self):
        l = (hash(self.refs), hash(self.execs))
        for c in sorted(self.contains.keys()):
            l = l + (c, hash(self.contains[c]))
        return hash(l)
    def __repr__(self):
        return " ".join([str(self.refs), str(self.execs), str(self.contains)]) 


class shellCacheLine(object):
    def __init__(self, execs):
        self.execs = codecache.internSet(execs)

    def __getstate__(self):
        return (self.execs)

    def __setstate__(self, state):
        (execs) = state
        self.__init__(execs)
    def __hash__(self):
        return hash(self.execs)
    def __repr__(self):
        return str(self.execs)

class CodeParserCache(MultiProcessCache):
    cache_file_name = "bb_codeparser.dat"
    # NOTE: you must increment this if you change how the parsers gather information,
    # so that an existing cache gets invalidated. Additionally you'll need
    # to increment __cache_version__ in cache.py in order to ensure that old
    # recipe caches don't trigger "Taskhash mismatch" errors.
    CACHE_VERSION = 9

    def __init__(self):
        MultiProcessCache.__init__(self)
        self.pythoncache = self.cachedata[0]
        self.shellcache = self.cachedata[1]
        self.pythoncacheextras = self.cachedata_extras[0]
        self.shellcacheextras = self.cachedata_extras[1]

        # To avoid duplication in the codeparser cache, keep
        # a lookup of hashes of objects we already have
        self.pythoncachelines = {}
        self.shellcachelines = {}

    def newPythonCacheLine(self, refs, execs, contains):
        cacheline = pythonCacheLine(refs, execs, contains)
        h = hash(cacheline)
        if h in self.pythoncachelines:
            return self.pythoncachelines[h]
        self.pythoncachelines[h] = cacheline
        return cacheline

    def newShellCacheLine(self, execs):
        cacheline = shellCacheLine(execs)
        h = hash(cacheline)
        if h in self.shellcachelines:
            return self.shellcachelines[h]
        self.shellcachelines[h] = cacheline
        return cacheline

    def init_cache(self, d):
        # Check if we already have the caches
        if self.pythoncache:
            return

        MultiProcessCache.init_cache(self, d)

        # cachedata gets re-assigned in the parent
        self.pythoncache = self.cachedata[0]
        self.shellcache = self.cachedata[1]

    def create_cachedata(self):
        data = [{}, {}]
        return data

codeparsercache = CodeParserCache()

def parser_cache_init(d):
    codeparsercache.init_cache(d)

def parser_cache_save():
    codeparsercache.save_extras()

def parser_cache_savemerge():
    codeparsercache.save_merge()

Logger = logging.getLoggerClass()
class BufferedLogger(Logger):
    def __init__(self, name, level=0, target=None):
        Logger.__init__(self, name)
        self.setLevel(level)
        self.buffer = []
        self.target = target

    def handle(self, record):
        self.buffer.append(record)

    def flush(self):
        for record in self.buffer:
            if self.target.isEnabledFor(record.levelno):
                self.target.handle(record)
        self.buffer = []

class PythonParser():
    getvars = (".getVar", ".appendVar", ".prependVar")
    getvarflags = (".getVarFlag", ".appendVarFlag", ".prependVarFlag")
    containsfuncs = ("bb.utils.contains", "base_contains")
    containsanyfuncs = ("bb.utils.contains_any",  "bb.utils.filter")
    execfuncs = ("bb.build.exec_func", "bb.build.exec_task")

    def warn(self, func, arg):
        """Warn about calls of bitbake APIs which pass a non-literal
        argument for the variable name, as we're not able to track such
        a reference.
        """

        try:
            funcstr = codegen.to_source(func)
            argstr = codegen.to_source(arg)
        except TypeError:
            self.log.debug(2, 'Failed to convert function and argument to source form')
        else:
            self.log.debug(1, self.unhandled_message % (funcstr, argstr))

    def visit_Call(self, node):
        name = self.called_node_name(node.func)
        if name and (name.endswith(self.getvars) or name.endswith(self.getvarflags) or name in self.containsfuncs or name in self.containsanyfuncs):
            if isinstance(node.args[0], ast.Str):
                varname = node.args[0].s
                if name in self.containsfuncs and isinstance(node.args[1], ast.Str):
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname].add(node.args[1].s)
                elif name in self.containsanyfuncs and isinstance(node.args[1], ast.Str):
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname].update(node.args[1].s.split())
                elif name.endswith(self.getvarflags):
                    if isinstance(node.args[1], ast.Str):
                        self.references.add('%s[%s]' % (varname, node.args[1].s))
                    else:
                        self.warn(node.func, node.args[1])
                else:
                    self.references.add(varname)
            else:
                self.warn(node.func, node.args[0])
        elif name and name.endswith(".expand"):
            if isinstance(node.args[0], ast.Str):
                value = node.args[0].s
                d = bb.data.init()
                parser = d.expandWithRefs(value, self.name)
                self.references |= parser.references
                self.execs |= parser.execs
                for varname in parser.contains:
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname] |= parser.contains[varname]
        elif name in self.execfuncs:
            if isinstance(node.args[0], ast.Str):
                self.var_execs.add(node.args[0].s)
            else:
                self.warn(node.func, node.args[0])
        elif name and isinstance(node.func, (ast.Name, ast.Attribute)):
            self.execs.add(name)

    def called_node_name(self, node):
        """Given a called node, return its original string form"""
        components = []
        while node:
            if isinstance(node, ast.Attribute):
                components.append(node.attr)
                node = node.value
            elif isinstance(node, ast.Name):
                components.append(node.id)
                return '.'.join(reversed(components))
            else:
                break

    def __init__(self, name, log):
        self.name = name
        self.var_execs = set()
        self.contains = {}
        self.execs = set()
        self.references = set()
        self.log = BufferedLogger('BitBake.Data.PythonParser', logging.DEBUG, log)

        self.unhandled_message = "in call of %s, argument '%s' is not a string literal"
        self.unhandled_message = "while parsing %s, %s" % (name, self.unhandled_message)

    def parse_python(self, node, lineno=0, filename="<string>"):
        if not node or not node.strip():
            return

        h = bbhash(str(node))

        if h in codeparsercache.pythoncache:
            self.references = set(codeparsercache.pythoncache[h].refs)
            self.execs = set(codeparsercache.pythoncache[h].execs)
            self.contains = {}
            for i in codeparsercache.pythoncache[h].contains:
                self.contains[i] = set(codeparsercache.pythoncache[h].contains[i])
            return

        if h in codeparsercache.pythoncacheextras:
            self.references = set(codeparsercache.pythoncacheextras[h].refs)
            self.execs = set(codeparsercache.pythoncacheextras[h].execs)
            self.contains = {}
            for i in codeparsercache.pythoncacheextras[h].contains:
                self.contains[i] = set(codeparsercache.pythoncacheextras[h].contains[i])
            return

        # We can't add to the linenumbers for compile, we can pad to the correct number of blank lines though
        node = "\n" * int(lineno) + node
        code = compile(check_indent(str(node)), filename, "exec",
                       ast.PyCF_ONLY_AST)

        for n in ast.walk(code):
            if n.__class__.__name__ == "Call":
                self.visit_Call(n)

        self.execs.update(self.var_execs)

        codeparsercache.pythoncacheextras[h] = codeparsercache.newPythonCacheLine(self.references, self.execs, self.contains)

class ShellParser():
    def __init__(self, name, log):
        self.funcdefs = set()
        self.allexecs = set()
        self.execs = set()
        self.log = BufferedLogger('BitBake.Data.%s' % name, logging.DEBUG, log)
        self.unhandled_template = "unable to handle non-literal command '%s'"
        self.unhandled_template = "while parsing %s, %s" % (name, self.unhandled_template)

    def parse_shell(self, value):
        """Parse the supplied shell code in a string, returning the external
        commands it executes.
        """

        h = bbhash(str(value))

        if h in codeparsercache.shellcache:
            self.execs = set(codeparsercache.shellcache[h].execs)
            return self.execs

        if h in codeparsercache.shellcacheextras:
            self.execs = set(codeparsercache.shellcacheextras[h].execs)
            return self.execs

        self._parse_shell(value)
        self.execs = set(cmd for cmd in self.allexecs if cmd not in self.funcdefs)

        codeparsercache.shellcacheextras[h] = codeparsercache.newShellCacheLine(self.execs)

        return self.execs

    def _parse_shell(self, value):
        try:
            tokens, _ = pyshyacc.parse(value, eof=True, debug=False)
        except pyshlex.NeedMore:
            raise sherrors.ShellSyntaxError("Unexpected EOF")

        self.process_tokens(tokens)

    def process_tokens(self, tokens):
        """Process a supplied portion of the syntax tree as returned by
        pyshyacc.parse.
        """

        def function_definition(value):
            self.funcdefs.add(value.name)
            return [value.body], None

        def case_clause(value):
            # Element 0 of each item in the case is the list of patterns, and
            # Element 1 of each item in the case is the list of commands to be
            # executed when that pattern matches.
            words = chain(*[item[0] for item in value.items])
            cmds  = chain(*[item[1] for item in value.items])
            return cmds, words

        def if_clause(value):
            main = chain(value.cond, value.if_cmds)
            rest = value.else_cmds
            if isinstance(rest, tuple) and rest[0] == "elif":
                return chain(main, if_clause(rest[1]))
            else:
                return chain(main, rest)

        def simple_command(value):
            return None, chain(value.words, (assign[1] for assign in value.assigns))

        token_handlers = {
            "and_or": lambda x: ((x.left, x.right), None),
            "async": lambda x: ([x], None),
            "brace_group": lambda x: (x.cmds, None),
            "for_clause": lambda x: (x.cmds, x.items),
            "function_definition": function_definition,
            "if_clause": lambda x: (if_clause(x), None),
            "pipeline": lambda x: (x.commands, None),
            "redirect_list": lambda x: ([x.cmd], None),
            "subshell": lambda x: (x.cmds, None),
            "while_clause": lambda x: (chain(x.condition, x.cmds), None),
            "until_clause": lambda x: (chain(x.condition, x.cmds), None),
            "simple_command": simple_command,
            "case_clause": case_clause,
        }

        def process_token_list(tokens):
            for token in tokens:
                if isinstance(token, list):
                    process_token_list(token)
                    continue
                name, value = token
                try:
                    more_tokens, words = token_handlers[name](value)
                except KeyError:
                    raise NotImplementedError("Unsupported token type " + name)

                if more_tokens:
                    self.process_tokens(more_tokens)

                if words:
                    self.process_words(words)

        process_token_list(tokens)

    def process_words(self, words):
        """Process a set of 'words' in pyshyacc parlance, which includes
        extraction of executed commands from $() blocks, as well as grabbing
        the command name argument.
        """

        words = list(words)
        for word in list(words):
            wtree = pyshlex.make_wordtree(word[1])
            for part in wtree:
                if not isinstance(part, list):
                    continue

                if part[0] in ('`', '$('):
                    command = pyshlex.wordtree_as_string(part[1:-1])
                    self._parse_shell(command)

                    if word[0] in ("cmd_name", "cmd_word"):
                        if word in words:
                            words.remove(word)

        usetoken = False
        for word in words:
            if word[0] in ("cmd_name", "cmd_word") or \
               (usetoken and word[0] == "TOKEN"):
                if "=" in word[1]:
                    usetoken = True
                    continue

                cmd = word[1]
                if cmd.startswith("$"):
                    self.log.debug(1, self.unhandled_template % cmd)
                elif cmd == "eval":
                    command = " ".join(word for _, word in words[1:])
                    self._parse_shell(command)
                else:
                    self.allexecs.add(cmd)
                break
