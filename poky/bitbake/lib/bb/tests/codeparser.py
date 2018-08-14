# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Test for codeparser.py
#
# Copyright (C) 2010 Chris Larson
# Copyright (C) 2012 Richard Purdie
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

import unittest
import logging
import bb

logger = logging.getLogger('BitBake.TestCodeParser')

# bb.data references bb.parse but can't directly import due to circular dependencies.
# Hack around it for now :( 
import bb.parse
import bb.data

class ReferenceTest(unittest.TestCase):
    def setUp(self):
        self.d = bb.data.init()

    def setEmptyVars(self, varlist):
        for k in varlist:
            self.d.setVar(k, "")

    def setValues(self, values):
        for k, v in values.items():
            self.d.setVar(k, v)

    def assertReferences(self, refs):
        self.assertEqual(self.references, refs)

    def assertExecs(self, execs):
        self.assertEqual(self.execs, execs)

    def assertContains(self, contains):
        self.assertEqual(self.contains, contains)

class VariableReferenceTest(ReferenceTest):

    def parseExpression(self, exp):
        parsedvar = self.d.expandWithRefs(exp, None)
        self.references = parsedvar.references

    def test_simple_reference(self):
        self.setEmptyVars(["FOO"])
        self.parseExpression("${FOO}")
        self.assertReferences(set(["FOO"]))

    def test_nested_reference(self):
        self.setEmptyVars(["BAR"])
        self.d.setVar("FOO", "BAR")
        self.parseExpression("${${FOO}}")
        self.assertReferences(set(["FOO", "BAR"]))

    def test_python_reference(self):
        self.setEmptyVars(["BAR"])
        self.parseExpression("${@d.getVar('BAR') + 'foo'}")
        self.assertReferences(set(["BAR"]))

class ShellReferenceTest(ReferenceTest):

    def parseExpression(self, exp):
        parsedvar = self.d.expandWithRefs(exp, None)
        parser = bb.codeparser.ShellParser("ParserTest", logger)
        parser.parse_shell(parsedvar.value)

        self.references = parsedvar.references
        self.execs = parser.execs

    def test_quotes_inside_assign(self):
        self.parseExpression('foo=foo"bar"baz')
        self.assertReferences(set([]))

    def test_quotes_inside_arg(self):
        self.parseExpression('sed s#"bar baz"#"alpha beta"#g')
        self.assertExecs(set(["sed"]))

    def test_arg_continuation(self):
        self.parseExpression("sed -i -e s,foo,bar,g \\\n *.pc")
        self.assertExecs(set(["sed"]))

    def test_dollar_in_quoted(self):
        self.parseExpression('sed -i -e "foo$" *.pc')
        self.assertExecs(set(["sed"]))

    def test_quotes_inside_arg_continuation(self):
        self.setEmptyVars(["bindir", "D", "libdir"])
        self.parseExpression("""
sed -i -e s#"moc_location=.*$"#"moc_location=${bindir}/moc4"# \\
-e s#"uic_location=.*$"#"uic_location=${bindir}/uic4"# \\
${D}${libdir}/pkgconfig/*.pc
""")
        self.assertReferences(set(["bindir", "D", "libdir"]))

    def test_assign_subshell_expansion(self):
        self.parseExpression("foo=$(echo bar)")
        self.assertExecs(set(["echo"]))

    def test_shell_unexpanded(self):
        self.setEmptyVars(["QT_BASE_NAME"])
        self.parseExpression('echo "${QT_BASE_NAME}"')
        self.assertExecs(set(["echo"]))
        self.assertReferences(set(["QT_BASE_NAME"]))

    def test_incomplete_varexp_single_quotes(self):
        self.parseExpression("sed -i -e 's:IP{:I${:g' $pc")
        self.assertExecs(set(["sed"]))


    def test_until(self):
        self.parseExpression("until false; do echo true; done")
        self.assertExecs(set(["false", "echo"]))
        self.assertReferences(set())

    def test_case(self):
        self.parseExpression("""
case $foo in
*)
bar
;;
esac
""")
        self.assertExecs(set(["bar"]))
        self.assertReferences(set())

    def test_assign_exec(self):
        self.parseExpression("a=b c='foo bar' alpha 1 2 3")
        self.assertExecs(set(["alpha"]))

    def test_redirect_to_file(self):
        self.setEmptyVars(["foo"])
        self.parseExpression("echo foo >${foo}/bar")
        self.assertExecs(set(["echo"]))
        self.assertReferences(set(["foo"]))

    def test_heredoc(self):
        self.setEmptyVars(["theta"])
        self.parseExpression("""
cat <<END
alpha
beta
${theta}
END
""")
        self.assertReferences(set(["theta"]))

    def test_redirect_from_heredoc(self):
        v = ["B", "SHADOW_MAILDIR", "SHADOW_MAILFILE", "SHADOW_UTMPDIR", "SHADOW_LOGDIR", "bindir"]
        self.setEmptyVars(v)
        self.parseExpression("""
cat <<END >${B}/cachedpaths
shadow_cv_maildir=${SHADOW_MAILDIR}
shadow_cv_mailfile=${SHADOW_MAILFILE}
shadow_cv_utmpdir=${SHADOW_UTMPDIR}
shadow_cv_logdir=${SHADOW_LOGDIR}
shadow_cv_passwd_dir=${bindir}
END
""")
        self.assertReferences(set(v))
        self.assertExecs(set(["cat"]))

#    def test_incomplete_command_expansion(self):
#        self.assertRaises(reftracker.ShellSyntaxError, reftracker.execs,
#                          bbvalue.shparse("cp foo`", self.d), self.d)

#    def test_rogue_dollarsign(self):
#        self.setValues({"D" : "/tmp"})
#        self.parseExpression("install -d ${D}$")
#        self.assertReferences(set(["D"]))
#        self.assertExecs(set(["install"]))


class PythonReferenceTest(ReferenceTest):

    def setUp(self):
        self.d = bb.data.init()
        if hasattr(bb.utils, "_context"):
            self.context = bb.utils._context
        else:
            import builtins
            self.context = builtins.__dict__

    def parseExpression(self, exp):
        parsedvar = self.d.expandWithRefs(exp, None)
        parser = bb.codeparser.PythonParser("ParserTest", logger)
        parser.parse_python(parsedvar.value)

        self.references = parsedvar.references | parser.references
        self.execs = parser.execs
        self.contains = parser.contains

    @staticmethod
    def indent(value):
        """Python Snippets have to be indented, python values don't have to
be. These unit tests are testing snippets."""
        return " " + value

    def test_getvar_reference(self):
        self.parseExpression("d.getVar('foo')")
        self.assertReferences(set(["foo"]))
        self.assertExecs(set())

    def test_getvar_computed_reference(self):
        self.parseExpression("d.getVar('f' + 'o' + 'o')")
        self.assertReferences(set())
        self.assertExecs(set())

    def test_getvar_exec_reference(self):
        self.parseExpression("eval('d.getVar(\"foo\")')")
        self.assertReferences(set())
        self.assertExecs(set(["eval"]))

    def test_var_reference(self):
        self.context["foo"] = lambda x: x
        self.setEmptyVars(["FOO"])
        self.parseExpression("foo('${FOO}')")
        self.assertReferences(set(["FOO"]))
        self.assertExecs(set(["foo"]))
        del self.context["foo"]

    def test_var_exec(self):
        for etype in ("func", "task"):
            self.d.setVar("do_something", "echo 'hi mom! ${FOO}'")
            self.d.setVarFlag("do_something", etype, True)
            self.parseExpression("bb.build.exec_func('do_something', d)")
            self.assertReferences(set([]))
            self.assertExecs(set(["do_something"]))

    def test_function_reference(self):
        self.context["testfunc"] = lambda msg: bb.msg.note(1, None, msg)
        self.d.setVar("FOO", "Hello, World!")
        self.parseExpression("testfunc('${FOO}')")
        self.assertReferences(set(["FOO"]))
        self.assertExecs(set(["testfunc"]))
        del self.context["testfunc"]

    def test_qualified_function_reference(self):
        self.parseExpression("time.time()")
        self.assertExecs(set(["time.time"]))

    def test_qualified_function_reference_2(self):
        self.parseExpression("os.path.dirname('/foo/bar')")
        self.assertExecs(set(["os.path.dirname"]))

    def test_qualified_function_reference_nested(self):
        self.parseExpression("time.strftime('%Y%m%d',time.gmtime())")
        self.assertExecs(set(["time.strftime", "time.gmtime"]))

    def test_function_reference_chained(self):
        self.context["testget"] = lambda: "\tstrip me "
        self.parseExpression("testget().strip()")
        self.assertExecs(set(["testget"]))
        del self.context["testget"]

    def test_contains(self):
        self.parseExpression('bb.utils.contains("TESTVAR", "one", "true", "false", d)')
        self.assertContains({'TESTVAR': {'one'}})

    def test_contains_multi(self):
        self.parseExpression('bb.utils.contains("TESTVAR", "one two", "true", "false", d)')
        self.assertContains({'TESTVAR': {'one two'}})

    def test_contains_any(self):
        self.parseExpression('bb.utils.contains_any("TESTVAR", "hello", "true", "false", d)')
        self.assertContains({'TESTVAR': {'hello'}})

    def test_contains_any_multi(self):
        self.parseExpression('bb.utils.contains_any("TESTVAR", "one two three", "true", "false", d)')
        self.assertContains({'TESTVAR': {'one', 'two', 'three'}})

    def test_contains_filter(self):
        self.parseExpression('bb.utils.filter("TESTVAR", "hello there world", d)')
        self.assertContains({'TESTVAR': {'hello', 'there', 'world'}})


class DependencyReferenceTest(ReferenceTest):

    pydata = """
d.getVar('somevar')
def test(d):
    foo = 'bar %s' % 'foo'
def test2(d):
    d.getVar(foo)
    d.getVar('bar', False)
    test2(d)

def a():
    \"\"\"some
    stuff
    \"\"\"
    return "heh"

test(d)

d.expand(d.getVar("something", False))
d.expand("${inexpand} somethingelse")
d.getVar(a(), False)
"""

    def test_python(self):
        self.d.setVar("FOO", self.pydata)
        self.setEmptyVars(["inexpand", "a", "test2", "test"])
        self.d.setVarFlags("FOO", {
            "func": True,
            "python": True,
            "lineno": 1,
            "filename": "example.bb",
        })

        deps, values = bb.data.build_dependencies("FOO", set(self.d.keys()), set(), set(), self.d)

        self.assertEqual(deps, set(["somevar", "bar", "something", "inexpand", "test", "test2", "a"]))


    shelldata = """
foo () {
bar
}
{
echo baz
$(heh)
eval `moo`
}
a=b
c=d
(
true && false
test -f foo
testval=something
$testval
) || aiee
! inverted
echo ${somevar}

case foo in
bar)
echo bar
;;
baz)
echo baz
;;
foo*)
echo foo
;;
esac
"""

    def test_shell(self):
        execs = ["bar", "echo", "heh", "moo", "true", "aiee"]
        self.d.setVar("somevar", "heh")
        self.d.setVar("inverted", "echo inverted...")
        self.d.setVarFlag("inverted", "func", True)
        self.d.setVar("FOO", self.shelldata)
        self.d.setVarFlags("FOO", {"func": True})
        self.setEmptyVars(execs)

        deps, values = bb.data.build_dependencies("FOO", set(self.d.keys()), set(), set(), self.d)

        self.assertEqual(deps, set(["somevar", "inverted"] + execs))


    def test_vardeps(self):
        self.d.setVar("oe_libinstall", "echo test")
        self.d.setVar("FOO", "foo=oe_libinstall; eval $foo")
        self.d.setVarFlag("FOO", "vardeps", "oe_libinstall")

        deps, values = bb.data.build_dependencies("FOO", set(self.d.keys()), set(), set(), self.d)

        self.assertEqual(deps, set(["oe_libinstall"]))

    def test_vardeps_expand(self):
        self.d.setVar("oe_libinstall", "echo test")
        self.d.setVar("FOO", "foo=oe_libinstall; eval $foo")
        self.d.setVarFlag("FOO", "vardeps", "${@'oe_libinstall'}")

        deps, values = bb.data.build_dependencies("FOO", set(self.d.keys()), set(), set(), self.d)

        self.assertEqual(deps, set(["oe_libinstall"]))

    def test_contains_vardeps(self):
        expr = '${@bb.utils.filter("TESTVAR", "somevalue anothervalue", d)} \
                ${@bb.utils.contains("TESTVAR", "testval testval2", "yetanothervalue", "", d)} \
                ${@bb.utils.contains("TESTVAR", "testval2 testval3", "blah", "", d)} \
                ${@bb.utils.contains_any("TESTVAR", "testval2 testval3", "lastone", "", d)}'
        parsedvar = self.d.expandWithRefs(expr, None)
        # Check contains
        self.assertEqual(parsedvar.contains, {'TESTVAR': {'testval2 testval3', 'anothervalue', 'somevalue', 'testval testval2', 'testval2', 'testval3'}})
        # Check dependencies
        self.d.setVar('ANOTHERVAR', expr)
        self.d.setVar('TESTVAR', 'anothervalue testval testval2')
        deps, values = bb.data.build_dependencies("ANOTHERVAR", set(self.d.keys()), set(), set(), self.d)
        self.assertEqual(sorted(values.splitlines()),
                         sorted([expr,
                          'TESTVAR{anothervalue} = Set',
                          'TESTVAR{somevalue} = Unset',
                          'TESTVAR{testval testval2} = Set',
                          'TESTVAR{testval2 testval3} = Unset',
                          'TESTVAR{testval2} = Set',
                          'TESTVAR{testval3} = Unset'
                          ]))
        # Check final value
        self.assertEqual(self.d.getVar('ANOTHERVAR').split(), ['anothervalue', 'yetanothervalue', 'lastone'])

    #Currently no wildcard support
    #def test_vardeps_wildcards(self):
    #    self.d.setVar("oe_libinstall", "echo test")
    #    self.d.setVar("FOO", "foo=oe_libinstall; eval $foo")
    #    self.d.setVarFlag("FOO", "vardeps", "oe_*")
    #    self.assertEquals(deps, set(["oe_libinstall"]))


