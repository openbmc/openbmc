# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2012, Intel Corporation.
# All rights reserved.
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
# DESCRIPTION
# This module implements the templating engine used by 'yocto-bsp' to
# create BSPs.  The BSP templates are simply the set of files expected
# to appear in a generated BSP, marked up with a small set of tags
# used to customize the output.  The engine parses through the
# templates and generates a Python program containing all the logic
# and input elements needed to display and retrieve BSP-specific
# information from the user.  The resulting program uses those results
# to generate the final BSP files.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] intel.com>
#

import os
import sys
from abc import ABCMeta, abstractmethod
from .tags import *
import shlex
import json
import subprocess
import shutil

class Line(metaclass=ABCMeta):
    """
    Generic (abstract) container representing a line that will appear
    in the BSP-generating program.
    """

    def __init__(self, line):
        self.line = line
        self.generated_line = ""
        self.prio = sys.maxsize
        self.discard = False

    @abstractmethod
    def gen(self, context = None):
        """
        Generate the final executable line that will appear in the
        BSP-generation program.
        """
        pass

    def escape(self, line):
        """
        Escape single and double quotes and backslashes until I find
        something better (re.escape() escapes way too much).
        """
        return line.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'")

    def parse_error(self, msg, lineno, line):
         raise SyntaxError("%s: %s" % (msg, line))


class NormalLine(Line):
    """
    Container for normal (non-tag) lines.
    """
    def __init__(self, line):
        Line.__init__(self, line)
        self.is_filename = False
        self.is_dirname = False
        self.out_filebase = None

    def gen(self, context = None):
        if self.is_filename:
            line = "current_file = \"" + os.path.join(self.out_filebase, self.escape(self.line)) + "\"; of = open(current_file, \"w\")"
        elif self.is_dirname:
            dirname = os.path.join(self.out_filebase, self.escape(self.line))
            line = "if not os.path.exists(\"" + dirname + "\"): os.mkdir(\"" + dirname + "\")"
        else:
            line = "of.write(\"" + self.escape(self.line) + "\\n\")"
        return line


class CodeLine(Line):
    """
    Container for Python code tag lines.
    """
    def __init__(self, line):
        Line.__init__(self, line)

    def gen(self, context = None):
        return self.line


class Assignment:
    """
    Representation of everything we know about {{=name }} tags.
    Instances of these are used by Assignment lines.
    """
    def __init__(self, start, end, name):
        self.start = start
        self.end = end
        self.name = name


class AssignmentLine(NormalLine):
    """
    Container for normal lines containing assignment tags.  Assignment
    tags must be in ascending order of 'start' value.
    """
    def __init__(self, line):
        NormalLine.__init__(self, line)
        self.assignments = []

    def add_assignment(self, start, end, name):
        self.assignments.append(Assignment(start, end, name))

    def gen(self, context = None):
        line = self.escape(self.line)

        for assignment in self.assignments:
            replacement = "\" + " + assignment.name + " + \""
            idx = line.find(ASSIGN_TAG)
            line = line[:idx] + replacement + line[idx + assignment.end - assignment.start:]
        if self.is_filename:
            return "current_file = \"" + os.path.join(self.out_filebase, line) + "\"; of = open(current_file, \"w\")"
        elif self.is_dirname:
            dirname = os.path.join(self.out_filebase, line)
            return "if not os.path.exists(\"" + dirname + "\"): os.mkdir(\"" + dirname + "\")"
        else:
            return "of.write(\"" + line + "\\n\")"


class InputLine(Line):
    """
    Base class for Input lines.
    """
    def __init__(self, props, tag, lineno):
        Line.__init__(self, tag)
        self.props = props
        self.lineno = lineno

        try:
            self.prio = int(props["prio"])
        except KeyError:
            self.prio = sys.maxsize

    def gen(self, context = None):
        try:
            depends_on = self.props["depends-on"]
            try:
                depends_on_val = self.props["depends-on-val"]
            except KeyError:
                self.parse_error("No 'depends-on-val' for 'depends-on' property",
                                 self.lineno, self.line)
        except KeyError:
            pass


class EditBoxInputLine(InputLine):
    """
    Base class for 'editbox' Input lines.

    props:
        name: example - "Load address"
        msg: example - "Please enter the load address"
    result:
        Sets the value of the variable specified by 'name' to
        whatever the user typed.
    """
    def __init__(self, props, tag, lineno):
        InputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        InputLine.gen(self, context)
        name = self.props["name"]
        if not name:
            self.parse_error("No input 'name' property found",
                             self.lineno, self.line)
        msg = self.props["msg"]
        if not msg:
            self.parse_error("No input 'msg' property found",
                             self.lineno, self.line)

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        msg += " [default: " + default_choice + "]"

        line = name + " = default(input(\"" + msg + " \"), " + name + ")"

        return line


class GitRepoEditBoxInputLine(EditBoxInputLine):
    """
    Base class for 'editbox' Input lines for user input of remote git
    repos.  This class verifies the existence and connectivity of the
    specified git repo.

    props:
        name: example - "Load address"
        msg: example - "Please enter the load address"
    result:
        Sets the value of the variable specified by 'name' to
        whatever the user typed.
    """
    def __init__(self, props, tag, lineno):
        EditBoxInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        EditBoxInputLine.gen(self, context)
        name = self.props["name"]
        if not name:
            self.parse_error("No input 'name' property found",
                             self.lineno, self.line)
        msg = self.props["msg"]
        if not msg:
            self.parse_error("No input 'msg' property found",
                             self.lineno, self.line)

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        msg += " [default: " + default_choice + "]"

        line = name + " = get_verified_git_repo(\"" + msg + "\"," + name + ")"

        return line


class FileEditBoxInputLine(EditBoxInputLine):
    """
    Base class for 'editbox' Input lines for user input of existing
    files.  This class verifies the existence of the specified file.

    props:
        name: example - "Load address"
        msg: example - "Please enter the load address"
    result:
        Sets the value of the variable specified by 'name' to
        whatever the user typed.
    """
    def __init__(self, props, tag, lineno):
        EditBoxInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        EditBoxInputLine.gen(self, context)
        name = self.props["name"]
        if not name:
            self.parse_error("No input 'name' property found",
                             self.lineno, self.line)
        msg = self.props["msg"]
        if not msg:
            self.parse_error("No input 'msg' property found",
                             self.lineno, self.line)

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        msg += " [default: " + default_choice + "]"

        line = name + " = get_verified_file(\"" + msg + "\"," + name + ", True)"

        return line


class BooleanInputLine(InputLine):
    """
    Base class for boolean Input lines.
    props:
        name: example - "keyboard"
        msg:  example - "Got keyboard?"
    result:
        Sets the value of the variable specified by 'name' to "yes" or "no"
        example - keyboard = "yes"
    """
    def __init__(self, props, tag, lineno):
        InputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        InputLine.gen(self, context)
        name = self.props["name"]
        if not name:
            self.parse_error("No input 'name' property found",
                             self.lineno, self.line)
        msg = self.props["msg"]
        if not msg:
            self.parse_error("No input 'msg' property found",
                             self.lineno, self.line)

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        msg += " [default: " + default_choice + "]"

        line = name + " = boolean(input(\"" + msg + " \"), " + name + ")"

        return line


class ListInputLine(InputLine, metaclass=ABCMeta):
    """
    Base class for List-based Input lines. e.g. Choicelist, Checklist.
    """

    def __init__(self, props, tag, lineno):
        InputLine.__init__(self, props, tag, lineno)
        self.choices = []

    def gen_choicepair_list(self):
        """Generate a list of 2-item val:desc lists from self.choices."""
        if not self.choices:
            return None

        choicepair_list = list()

        for choice in self.choices:
            choicepair = []
            choicepair.append(choice.val)
            choicepair.append(choice.desc)
            choicepair_list.append(choicepair)

        return choicepair_list

    def gen_degenerate_choicepair_list(self, choices):
        """Generate a list of 2-item val:desc with val=desc from passed-in choices."""
        choicepair_list = list()

        for choice in choices:
            choicepair = []
            choicepair.append(choice)
            choicepair.append(choice)
            choicepair_list.append(choicepair)

        return choicepair_list

    def exec_listgen_fn(self, context = None):
        """
        Execute the list-generating function contained as a string in
        the "gen" property.
        """
        retval = None
        try:
            fname = self.props["gen"]
            modsplit = fname.split('.')
            mod_fn = modsplit.pop()
            mod = '.'.join(modsplit)

            __import__(mod)
            # python 2.7 has a better way to do this using importlib.import_module
            m = sys.modules[mod]

            fn = getattr(m, mod_fn)
            if not fn:
                self.parse_error("couldn't load function specified for 'gen' property ",
                                 self.lineno, self.line)
            retval = fn(context)
            if not retval:
                self.parse_error("function specified for 'gen' property returned nothing ",
                                 self.lineno, self.line)
        except KeyError:
            pass

        return retval

    def gen_choices_str(self, choicepairs):
        """
        Generate a numbered list of choices from a list of choicepairs
        for display to the user.
        """
        choices_str = ""

        for i, choicepair in enumerate(choicepairs):
            choices_str += "\t" + str(i + 1) + ") " + choicepair[1] + "\n"

        return choices_str

    def gen_choices_val_str(self, choicepairs):
        """
        Generate an array of choice values corresponding to the
        numbered list generated by gen_choices_str().
        """
        choices_val_list = "["

        for i, choicepair in enumerate(choicepairs):
            choices_val_list += "\"" + choicepair[0] + "\","
        choices_val_list += "]"

        return choices_val_list

    def gen_choices_val_list(self, choicepairs):
        """
        Generate an array of choice values corresponding to the
        numbered list generated by gen_choices_str().
        """
        choices_val_list = []

        for i, choicepair in enumerate(choicepairs):
            choices_val_list.append(choicepair[0])

        return choices_val_list

    def gen_choices_list(self, context = None, checklist = False):
        """
        Generate an array of choice values corresponding to the
        numbered list generated by gen_choices_str().
        """
        choices = self.exec_listgen_fn(context)
        if choices:
            if len(choices) == 0:
                self.parse_error("No entries available for input list",
                                 self.lineno, self.line)
            choicepairs = self.gen_degenerate_choicepair_list(choices)
        else:
            if len(self.choices) == 0:
                self.parse_error("No entries available for input list",
                                 self.lineno, self.line)
            choicepairs = self.gen_choicepair_list()

        return choicepairs

    def gen_choices(self, context = None, checklist = False):
        """
        Generate an array of choice values corresponding to the
        numbered list generated by gen_choices_str(), display it to
        the user, and process the result.
        """
        msg = self.props["msg"]
        if not msg:
            self.parse_error("No input 'msg' property found",
                             self.lineno, self.line)

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        msg += " [default: " + default_choice + "]"

        choicepairs = self.gen_choices_list(context, checklist)

        choices_str = self.gen_choices_str(choicepairs)
        choices_val_list = self.gen_choices_val_list(choicepairs)
        if checklist:
            choiceval = default(find_choicevals(input(msg + "\n" + choices_str), choices_val_list), default_choice)
        else:
            choiceval = default(find_choiceval(input(msg + "\n" + choices_str), choices_val_list), default_choice)

        return choiceval


def find_choiceval(choice_str, choice_list):
    """
    Take number as string and return val string from choice_list,
    empty string if oob.  choice_list is a simple python list.
    """
    choice_val = ""

    try:
        choice_idx = int(choice_str)
        if choice_idx <= len(choice_list):
            choice_idx -= 1
            choice_val = choice_list[choice_idx]
    except ValueError:
        pass

    return choice_val


def find_choicevals(choice_str, choice_list):
    """
    Take numbers as space-separated string and return vals list from
    choice_list, empty list if oob.  choice_list is a simple python
    list.
    """
    choice_vals = []

    choices = choice_str.split()
    for choice in choices:
        choice_vals.append(find_choiceval(choice, choice_list))

    return choice_vals


def default(input_str, name):
    """
    Return default if no input_str, otherwise stripped input_str.
    """
    if not input_str:
        return name

    return input_str.strip()


def verify_git_repo(giturl):
    """
    Verify that the giturl passed in can be connected to.  This can be
    used as a check for the existence of the given repo and/or basic
    git remote connectivity.

    Returns True if the connection was successful, fals otherwise
    """
    if not giturl:
        return False

    gitcmd = "git ls-remote %s > /dev/null 2>&1" % (giturl)
    rc = subprocess.call(gitcmd, shell=True)
    if rc == 0:
        return True

    return False


def get_verified_git_repo(input_str, name):
    """
    Return git repo if verified, otherwise loop forever asking user
    for filename.
    """
    msg = input_str.strip() + " "

    giturl = default(input(msg), name)

    while True:
        if verify_git_repo(giturl):
            return giturl
        giturl = default(input(msg), name)


def get_verified_file(input_str, name, filename_can_be_null):
    """
    Return filename if the file exists, otherwise loop forever asking
    user for filename.
    """
    msg = input_str.strip() + " "

    filename = default(input(msg), name)

    while True:
        if not filename and filename_can_be_null:
            return filename
        if os.path.isfile(filename):
            return filename
        filename = default(input(msg), name)


def replace_file(replace_this, with_this):
    """
    Replace the given file with the contents of filename, retaining
    the original filename.
    """
    try:
        replace_this.close()
        shutil.copy(with_this, replace_this.name)
    except IOError:
        pass


def boolean(input_str, name):
    """
    Return lowercase version of first char in string, or value in name.
    """
    if not input_str:
        return name

    str = input_str.lower().strip()
    if str and str[0] == "y" or str[0] == "n":
        return str[0]
    else:
        return name


def strip_base(input_str):
    """
    strip '/base' off the end of input_str, so we can use 'base' in
    the branch names we present to the user.
    """
    if input_str and input_str.endswith("/base"):
        return input_str[:-len("/base")]
    return input_str.strip()


deferred_choices = {}

def gen_choices_defer(input_line, context, checklist = False):
    """
    Save the context hashed the name of the input item, which will be
    passed to the gen function later.
    """
    name = input_line.props["name"]

    try:
        nameappend = input_line.props["nameappend"]
    except KeyError:
        nameappend = ""

    try:
        branches_base = input_line.props["branches_base"]
    except KeyError:
        branches_base = ""

    filename = input_line.props["filename"]

    closetag_start = filename.find(CLOSE_TAG)

    if closetag_start != -1:
        filename = filename[closetag_start + len(CLOSE_TAG):]

    filename = filename.strip()
    filename = os.path.splitext(filename)[0]

    captured_context = capture_context(context)
    context["filename"] = filename
    captured_context["filename"] = filename
    context["nameappend"] = nameappend
    captured_context["nameappend"] = nameappend
    context["branches_base"] = branches_base
    captured_context["branches_base"] = branches_base

    deferred_choice = (input_line, captured_context, checklist)
    key = name + "_" + filename + "_" + nameappend
    deferred_choices[key] = deferred_choice


def invoke_deferred_choices(name):
    """
    Invoke the choice generation function using the context hashed by
    'name'.
    """
    deferred_choice = deferred_choices[name]
    input_line = deferred_choice[0]
    context = deferred_choice[1]
    checklist = deferred_choice[2]

    context["name"] = name

    choices = input_line.gen_choices(context, checklist)

    return choices


class ChoicelistInputLine(ListInputLine):
    """
    Base class for choicelist Input lines.
    props:
        name: example - "xserver_choice"
        msg:  example - "Please select an xserver for this machine"
    result:
        Sets the value of the variable specified by 'name' to whichever Choice was chosen
        example - xserver_choice = "xserver_vesa"
    """
    def __init__(self, props, tag, lineno):
        ListInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        InputLine.gen(self, context)

        gen_choices_defer(self, context)
        name = self.props["name"]
        nameappend = context["nameappend"]
        filename = context["filename"]

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        line = name + " = default(invoke_deferred_choices(\"" + name + "_" + filename + "_" + nameappend + "\"), \"" + default_choice + "\")"

        return line


class ListValInputLine(InputLine):
    """
    Abstract base class for choice and checkbox Input lines.
    """
    def __init__(self, props, tag, lineno):
        InputLine.__init__(self, props, tag, lineno)

        try:
            self.val = self.props["val"]
        except KeyError:
            self.parse_error("No input 'val' property found", self.lineno, self.line)

        try:
            self.desc = self.props["msg"]
        except KeyError:
            self.parse_error("No input 'msg' property found", self.lineno, self.line)


class ChoiceInputLine(ListValInputLine):
    """
    Base class for choicelist item Input lines.
    """
    def __init__(self, props, tag, lineno):
        ListValInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        return None


class ChecklistInputLine(ListInputLine):
    """
    Base class for checklist Input lines.
    """
    def __init__(self, props, tag, lineno):
        ListInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        InputLine.gen(self, context)

        gen_choices_defer(self, context, True)
        name = self.props["name"]
        nameappend = context["nameappend"]
        filename = context["filename"]

        try:
            default_choice = self.props["default"]
        except KeyError:
            default_choice = ""

        line = name + " = default(invoke_deferred_choices(\"" + name + "_" + filename + "_" + nameappend + "\"), \"" + default_choice + "\")"

        return line


class CheckInputLine(ListValInputLine):
    """
    Base class for checklist item Input lines.
    """
    def __init__(self, props, tag, lineno):
        ListValInputLine.__init__(self, props, tag, lineno)

    def gen(self, context = None):
        return None


dirname_substitutions = {}

class SubstrateBase(object):
    """
    Base class for both expanded and unexpanded file and dir container
    objects.
    """
    def __init__(self, filename, filebase, out_filebase):
        self.filename = filename
        self.filebase = filebase
        self.translated_filename = filename
        self.out_filebase = out_filebase
        self.raw_lines = []
        self.expanded_lines = []
        self.prev_choicelist = None

    def parse_error(self, msg, lineno, line):
         raise SyntaxError("%s: [%s: %d]: %s" % (msg, self.filename, lineno, line))

    def expand_input_tag(self, tag, lineno):
        """
        Input tags consist of the word 'input' at the beginning,
        followed by name:value property pairs which are converted into
        a dictionary.
        """
        propstr = tag[len(INPUT_TAG):]

        props = dict(prop.split(":", 1) for prop in shlex.split(propstr))
        props["filename"] = self.filename

        input_type = props[INPUT_TYPE_PROPERTY]
        if not props[INPUT_TYPE_PROPERTY]:
            self.parse_error("No input 'type' property found", lineno, tag)

        if input_type == "boolean":
            return BooleanInputLine(props, tag, lineno)
        if input_type == "edit":
            return EditBoxInputLine(props, tag, lineno)
        if input_type == "edit-git-repo":
            return GitRepoEditBoxInputLine(props, tag, lineno)
        if input_type == "edit-file":
            return FileEditBoxInputLine(props, tag, lineno)
        elif input_type == "choicelist":
            self.prev_choicelist = ChoicelistInputLine(props, tag, lineno)
            return self.prev_choicelist
        elif input_type == "choice":
            if not self.prev_choicelist:
                self.parse_error("Found 'choice' input tag but no previous choicelist",
                                 lineno, tag)
            choice = ChoiceInputLine(props, tag, lineno)
            self.prev_choicelist.choices.append(choice)
            return choice
        elif input_type == "checklist":
            return ChecklistInputLine(props, tag, lineno)
        elif input_type == "check":
            return CheckInputLine(props, tag, lineno)

    def expand_assignment_tag(self, start, line, lineno):
        """
        Expand all tags in a line.
        """
        expanded_line = AssignmentLine(line.rstrip())

        while start != -1:
            end = line.find(CLOSE_TAG, start)
            if end == -1:
                self.parse_error("No close tag found for assignment tag", lineno, line)
            else:
                name = line[start + len(ASSIGN_TAG):end].strip()
                expanded_line.add_assignment(start, end + len(CLOSE_TAG), name)
                start = line.find(ASSIGN_TAG, end)

        return expanded_line

    def expand_tag(self, line, lineno):
        """
        Returns a processed tag line, or None if there was no tag

        The rules for tags are very simple:
            - No nested tags
            - Tags start with {{ and end with }}
            - An assign tag, {{=, can appear anywhere and will
              be replaced with what the assignment evaluates to
            - Any other tag occupies the whole line it is on
                - if there's anything else on the tag line, it's an error
                - if it starts with 'input', it's an input tag and
                  will only be used for prompting and setting variables
                - anything else is straight Python
                - tags are in effect only until the next blank line or tag or 'pass' tag
                - we don't have indentation in tags, but we need some way to end a block
                  forcefully without blank lines or other tags - that's the 'pass' tag
                - todo: implement pass tag
                - directories and filenames can have tags as well, but only assignment
                  and 'if' code lines
                - directories and filenames are the only case where normal tags can
                  coexist with normal text on the same 'line'
        """
        start = line.find(ASSIGN_TAG)
        if start != -1:
            return self.expand_assignment_tag(start, line, lineno)

        start = line.find(OPEN_TAG)
        if start == -1:
            return None

        end = line.find(CLOSE_TAG, 0)
        if end == -1:
             self.parse_error("No close tag found for open tag", lineno, line)

        tag = line[start + len(OPEN_TAG):end].strip()

        if not tag.lstrip().startswith(INPUT_TAG):
            return CodeLine(tag)

        return self.expand_input_tag(tag, lineno)

    def append_translated_filename(self, filename):
        """
        Simply append filename to translated_filename
        """
        self.translated_filename = os.path.join(self.translated_filename, filename)

    def get_substituted_file_or_dir_name(self, first_line, tag):
        """
        If file or dir names contain name substitutions, return the name
        to substitute.  Note that this is just the file or dirname and
        doesn't include the path.
        """
        filename = first_line.find(tag)
        if filename != -1:
            filename += len(tag)
        substituted_filename = first_line[filename:].strip()
        this = substituted_filename.find(" this")
        if this != -1:
            head, tail = os.path.split(self.filename)
            substituted_filename = substituted_filename[:this + 1] + tail
            if tag == DIRNAME_TAG: # get rid of .noinstall in dirname
                substituted_filename = substituted_filename.split('.')[0]

        return substituted_filename

    def get_substituted_filename(self, first_line):
        """
        If a filename contains a name substitution, return the name to
        substitute.  Note that this is just the filename and doesn't
        include the path.
        """
        return self.get_substituted_file_or_dir_name(first_line, FILENAME_TAG)

    def get_substituted_dirname(self, first_line):
        """
        If a dirname contains a name substitution, return the name to
        substitute.  Note that this is just the dirname and doesn't
        include the path.
        """
        return self.get_substituted_file_or_dir_name(first_line, DIRNAME_TAG)

    def substitute_filename(self, first_line):
        """
        Find the filename in first_line and append it to translated_filename.
        """
        substituted_filename = self.get_substituted_filename(first_line)
        self.append_translated_filename(substituted_filename);

    def substitute_dirname(self, first_line):
        """
        Find the dirname in first_line and append it to translated_filename.
        """
        substituted_dirname = self.get_substituted_dirname(first_line)
        self.append_translated_filename(substituted_dirname);

    def is_filename_substitution(self, line):
        """
        Do we have a filename subustition?
        """
        if line.find(FILENAME_TAG) != -1:
            return True
        return False

    def is_dirname_substitution(self, line):
        """
        Do we have a dirname subustition?
        """
        if line.find(DIRNAME_TAG) != -1:
            return True
        return False

    def translate_dirname(self, first_line):
        """
        Just save the first_line mapped by filename.  The later pass
        through the directories will look for a dirname.noinstall
        match and grab the substitution line.
        """
        dirname_substitutions[self.filename] = first_line

    def translate_dirnames_in_path(self, path):
        """
        Translate dirnames below this file or dir, not including tail.
        dirname_substititions is keyed on actual untranslated filenames.
        translated_path contains the subsititutions for each element.
        """
        remainder = path[len(self.filebase)+1:]
        translated_path = untranslated_path = self.filebase

        untranslated_dirs = remainder.split(os.sep)

        for dir in untranslated_dirs:
            key = os.path.join(untranslated_path, dir + '.noinstall')
            try:
                first_line = dirname_substitutions[key]
            except KeyError:
                translated_path = os.path.join(translated_path, dir)
                untranslated_path = os.path.join(untranslated_path, dir)
                continue
            substituted_dir = self.get_substituted_dirname(first_line)
            translated_path = os.path.join(translated_path, substituted_dir)
            untranslated_path = os.path.join(untranslated_path, dir)

        return translated_path

    def translate_file_or_dir_name(self):
        """
        Originally we were allowed to use open/close/assign tags and python
        code in the filename, which fit in nicely with the way we
        processed the templates and generated code.  Now that we can't
        do that, we make those tags proper file contents and have this
        pass substitute the nice but non-functional names with those
        'strange' ones, and then proceed as usual.

        So, if files or matching dir<.noinstall> files contain
        filename substitutions, this function translates them into the
        corresponding 'strange' names, which future passes will expand
        as they always have.  The resulting pathname is kept in the
        file or directory's translated_filename.  Another way to think
        about it is that self.filename is the input filename, and
        translated_filename is the output filename before expansion.
        """
        # remove leaf file or dirname
        head, tail = os.path.split(self.filename)
        translated_path = self.translate_dirnames_in_path(head)
        self.translated_filename = translated_path

        # This is a dirname - does it have a matching .noinstall with
        # a substitution?  If so, apply the dirname subsititution.
        if not os.path.isfile(self.filename):
            key = self.filename + ".noinstall"
            try:
                first_line = dirname_substitutions[key]
            except KeyError:
                self.append_translated_filename(tail)
                return
            self.substitute_dirname(first_line)
            return

        f = open(self.filename)
        first_line = f.readline()
        f.close()

        # This is a normal filename not needing translation, just use
        # it as-is.
        if not first_line or not first_line.startswith("#"):
            self.append_translated_filename(tail)
            return

        # If we have a filename substitution (first line in the file
        # is a FILENAME_TAG line) do the substitution now.  If we have
        # a dirname substitution (DIRNAME_TAG in dirname.noinstall
        # meta-file), hash it so we can apply it when we see the
        # matching dirname later.  Otherwise we have a regular
        # filename, just use it as-is.
        if self.is_filename_substitution(first_line):
            self.substitute_filename(first_line)
        elif self.is_dirname_substitution(first_line):
            self.translate_dirname(first_line)
        else:
            self.append_translated_filename(tail)

    def expand_file_or_dir_name(self):
        """
        Expand file or dir names into codeline.  Dirnames and
        filenames can only have assignments or if statements.  First
        translate if statements into CodeLine + (dirname or filename
        creation).
        """
        lineno = 0

        line = self.translated_filename[len(self.filebase):]
        if line.startswith("/"):
            line = line[1:]
        opentag_start = -1

        start = line.find(OPEN_TAG)
        while start != -1:
            if not line[start:].startswith(ASSIGN_TAG):
                opentag_start = start
                break
            start += len(ASSIGN_TAG)
            start = line.find(OPEN_TAG, start)

        if opentag_start != -1:
            end = line.find(CLOSE_TAG, opentag_start)
            if end == -1:
                self.parse_error("No close tag found for open tag", lineno, line)
            # we have a {{ tag i.e. code
            tag = line[opentag_start + len(OPEN_TAG):end].strip()
            if not tag.lstrip().startswith(IF_TAG):
                self.parse_error("Only 'if' tags are allowed in file or directory names",
                                 lineno, line)
            self.expanded_lines.append(CodeLine(tag))

            # everything after }} is the actual filename (possibly with assignments)
            # everything before is the pathname
            line = line[:opentag_start] + line[end + len(CLOSE_TAG):].strip()

        assign_start = line.find(ASSIGN_TAG)
        if assign_start != -1:
            assignment_tag = self.expand_assignment_tag(assign_start, line, lineno)
            if isinstance(self, SubstrateFile):
                assignment_tag.is_filename = True
                assignment_tag.out_filebase = self.out_filebase
            elif isinstance(self, SubstrateDir):
                assignment_tag.is_dirname = True
                assignment_tag.out_filebase = self.out_filebase
            self.expanded_lines.append(assignment_tag)
            return

        normal_line = NormalLine(line)
        if isinstance(self, SubstrateFile):
            normal_line.is_filename = True
            normal_line.out_filebase = self.out_filebase
        elif isinstance(self, SubstrateDir):
            normal_line.is_dirname = True
            normal_line.out_filebase = self.out_filebase
        self.expanded_lines.append(normal_line)

    def expand(self):
        """
        Expand the file or dir name first, eventually this ends up
        creating the file or dir.
        """
        self.translate_file_or_dir_name()
        self.expand_file_or_dir_name()


class SubstrateFile(SubstrateBase):
    """
    Container for both expanded and unexpanded substrate files.
    """
    def __init__(self, filename, filebase, out_filebase):
        SubstrateBase.__init__(self, filename, filebase, out_filebase)

    def read(self):
        if self.raw_lines:
            return
        f = open(self.filename)
        self.raw_lines = f.readlines()

    def expand(self):
        """Expand the contents of all template tags in the file."""
        SubstrateBase.expand(self)
        self.read()

        for lineno, line in enumerate(self.raw_lines):
            # only first line can be a filename substitition
            if lineno == 0 and line.startswith("#") and FILENAME_TAG in line:
                continue # skip it - we've already expanded it
            expanded_line = self.expand_tag(line, lineno + 1) # humans not 0-based
            if not expanded_line:
                expanded_line = NormalLine(line.rstrip())
            self.expanded_lines.append(expanded_line)

    def gen(self, context = None):
        """Generate the code that generates the BSP."""
        base_indent = 0

        indent = new_indent = base_indent

        for line in self.expanded_lines:
            genline = line.gen(context)
            if not genline:
                continue
            if isinstance(line, InputLine):
                line.generated_line = genline
                continue
            if genline.startswith(OPEN_START):
                if indent == 1:
                    base_indent = 1
            if indent:
                if genline == BLANKLINE_STR or (not genline.startswith(NORMAL_START)
                                                and not genline.startswith(OPEN_START)):
                    indent = new_indent = base_indent
            if genline.endswith(":"):
                new_indent = base_indent + 1
            line.generated_line = (indent * INDENT_STR) + genline
            indent = new_indent


class SubstrateDir(SubstrateBase):
    """
    Container for both expanded and unexpanded substrate dirs.
    """
    def __init__(self, filename, filebase, out_filebase):
        SubstrateBase.__init__(self, filename, filebase, out_filebase)

    def expand(self):
        SubstrateBase.expand(self)

    def gen(self, context = None):
        """Generate the code that generates the BSP."""
        indent = new_indent = 0
        for line in self.expanded_lines:
            genline = line.gen(context)
            if not genline:
                continue
            if genline.endswith(":"):
                new_indent = 1
            else:
                new_indent = 0
            line.generated_line = (indent * INDENT_STR) + genline
            indent = new_indent


def expand_target(target, all_files, out_filebase):
    """
    Expand the contents of all template tags in the target.  This
    means removing tags and categorizing or creating lines so that
    future passes can process and present input lines and generate the
    corresponding lines of the Python program that will be exec'ed to
    actually produce the final BSP.  'all_files' includes directories.
    """
    for root, dirs, files in os.walk(target):
        for file in files:
            if file.endswith("~") or file.endswith("#"):
                continue
            f = os.path.join(root, file)
            sfile = SubstrateFile(f, target, out_filebase)
            sfile.expand()
            all_files.append(sfile)

        for dir in dirs:
            d = os.path.join(root, dir)
            sdir = SubstrateDir(d, target, out_filebase)
            sdir.expand()
            all_files.append(sdir)


def gen_program_machine_lines(machine, program_lines):
    """
    Use the input values we got from the command line.
    """
    line = "machine = \"" + machine + "\""
    program_lines.append(line)

    line = "layer_name = \"" + machine + "\""
    program_lines.append(line)


def sort_inputlines(input_lines):
    """Sort input lines according to priority (position)."""
    input_lines.sort(key = lambda l: l.prio)


def find_parent_dependency(lines, depends_on):
    for i, line in lines:
        if isinstance(line, CodeLine):
            continue
        if line.props["name"] == depends_on:
            return i

    return -1


def process_inputline_dependencies(input_lines, all_inputlines):
    """If any input lines depend on others, put the others first."""
    for line in input_lines:
        if isinstance(line, InputLineGroup):
            group_inputlines = []
            process_inputline_dependencies(line.group, group_inputlines)
            line.group = group_inputlines
            all_inputlines.append(line)
            continue

        if isinstance(line, CodeLine) or isinstance(line, NormalLine):
            all_inputlines.append(line)
            continue

        try:
            depends_on = line.props["depends-on"]
            depends_codeline = "if " + line.props["depends-on"] + " == \"" + line.props["depends-on-val"] + "\":"
            all_inputlines.append(CodeLine(depends_codeline))
            all_inputlines.append(line)
        except KeyError:
            all_inputlines.append(line)


def conditional_filename(filename):
    """
    Check if the filename itself contains a conditional statement.  If
    so, return a codeline for it.
    """
    opentag_start = filename.find(OPEN_TAG)

    if opentag_start != -1:
        if filename[opentag_start:].startswith(ASSIGN_TAG):
            return None
        end = filename.find(CLOSE_TAG, opentag_start)
        if end == -1:
            print("No close tag found for open tag in filename %s" % filename)
            sys.exit(1)

        # we have a {{ tag i.e. code
        tag = filename[opentag_start + len(OPEN_TAG):end].strip()
        if not tag.lstrip().startswith(IF_TAG):
            print("Only 'if' tags are allowed in file or directory names, filename: %s" % filename)
            sys.exit(1)

        return CodeLine(tag)

    return None


class InputLineGroup(InputLine):
    """
    InputLine that does nothing but group other input lines
    corresponding to all the input lines in a SubstrateFile so they
    can be generated as a group.  prio is the only property used.
    """
    def __init__(self, codeline):
        InputLine.__init__(self, {}, "", 0)
        self.group = []
        self.prio = sys.maxsize
        self.group.append(codeline)

    def append(self, line):
        self.group.append(line)
        if line.prio < self.prio:
            self.prio = line.prio

    def len(self):
        return len(self.group)


def gather_inputlines(files):
    """
    Gather all the InputLines - we want to generate them first.
    """
    all_inputlines = []
    input_lines = []

    for file in files:
        if isinstance(file, SubstrateFile):
            group = None
            basename = os.path.basename(file.translated_filename)

            codeline = conditional_filename(basename)
            if codeline:
                group = InputLineGroup(codeline)

            have_condition = False
            condition_to_write = None
            for line in file.expanded_lines:
                if isinstance(line, CodeLine):
                    have_condition = True
                    condition_to_write = line
                    continue
                if isinstance(line, InputLine):
                    if group:
                        if condition_to_write:
                            condition_to_write.prio = line.prio
                            condition_to_write.discard = True
                            group.append(condition_to_write)
                            condition_to_write = None
                        group.append(line)
                    else:
                        if condition_to_write:
                            condition_to_write.prio = line.prio
                            condition_to_write.discard = True
                            input_lines.append(condition_to_write)
                            condition_to_write = None
                        input_lines.append(line)
                else:
                    if condition_to_write:
                        condition_to_write = None
                    if have_condition:
                        if not line.line.strip():
                            line.discard = True
                            input_lines.append(line)
                    have_condition = False

            if group and group.len() > 1:
                input_lines.append(group)

    sort_inputlines(input_lines)
    process_inputline_dependencies(input_lines, all_inputlines)

    return all_inputlines


def run_program_lines(linelist, codedump):
    """
    For a single file, print all the python code into a buf and execute it.
    """
    buf = "\n".join(linelist)

    if codedump:
        of = open("bspgen.out", "w")
        of.write(buf)
        of.close()
    exec(buf)


def gen_target(files, context = None):
    """
    Generate the python code for each file.
    """
    for file in files:
        file.gen(context)


def gen_program_header_lines(program_lines):
    """
    Generate any imports we need.
    """
    program_lines.append("current_file = \"\"")


def gen_supplied_property_vals(properties, program_lines):
    """
    Generate user-specified entries for input values instead of
    generating input prompts.
    """
    for name, val in properties.items():
        program_line = name + " = \"" + val + "\""
        program_lines.append(program_line)


def gen_initial_property_vals(input_lines, program_lines):
    """
    Generate null or default entries for input values, so we don't
    have undefined variables.
    """
    for line in input_lines:
        if isinstance(line, InputLineGroup):
            gen_initial_property_vals(line.group, program_lines)
            continue

        if isinstance(line, InputLine):
            try:
                name = line.props["name"]
                try:
                    default_val = "\"" + line.props["default"] + "\""
                except:
                    default_val = "\"\""
                program_line = name + " = " + default_val
                program_lines.append(program_line)
            except KeyError:
                pass


def gen_program_input_lines(input_lines, program_lines, context, in_group = False):
    """
    Generate only the input lines used for prompting the user.  For
    that, we only have input lines and CodeLines that affect the next
    input line.
    """
    indent = new_indent = 0

    for line in input_lines:
        if isinstance(line, InputLineGroup):
            gen_program_input_lines(line.group, program_lines, context, True)
            continue
        if not line.line.strip():
            continue

        genline = line.gen(context)
        if not genline:
            continue
        if genline.endswith(":"):
            new_indent += 1
        else:
            if indent > 1 or (not in_group and indent):
                new_indent -= 1

        line.generated_line = (indent * INDENT_STR) + genline
        program_lines.append(line.generated_line)

        indent = new_indent


def gen_program_lines(target_files, program_lines):
    """
    Generate the program lines that make up the BSP generation
    program.  This appends the generated lines of all target_files to
    program_lines, and skips input lines, which are dealt with
    separately, or omitted.
    """
    for file in target_files:
        if file.filename.endswith("noinstall"):
            continue

        for line in file.expanded_lines:
            if isinstance(line, InputLine):
                continue
            if line.discard:
                continue

            program_lines.append(line.generated_line)


def create_context(machine, arch, scripts_path):
    """
    Create a context object for use in deferred function invocation.
    """
    context = {}

    context["machine"] = machine
    context["arch"] = arch
    context["scripts_path"] = scripts_path

    return context


def capture_context(context):
    """
    Create a context object for use in deferred function invocation.
    """
    captured_context = {}

    captured_context["machine"] = context["machine"]
    captured_context["arch"] = context["arch"]
    captured_context["scripts_path"] = context["scripts_path"]

    return captured_context


def expand_targets(context, bsp_output_dir, expand_common=True):
    """
    Expand all the tags in both the common and machine-specific
    'targets'.

    If expand_common is False, don't expand the common target (this
    option is used to create special-purpose layers).
    """
    target_files = []

    machine = context["machine"]
    arch = context["arch"]
    scripts_path = context["scripts_path"]

    lib_path = scripts_path + '/lib'
    bsp_path = lib_path + '/bsp'
    arch_path = bsp_path + '/substrate/target/arch'

    if expand_common:
        common = os.path.join(arch_path, "common")
        expand_target(common, target_files, bsp_output_dir)

    arches = os.listdir(arch_path)
    if arch not in arches or arch == "common":
        print("Invalid karch, exiting\n")
        sys.exit(1)

    target = os.path.join(arch_path, arch)
    expand_target(target, target_files, bsp_output_dir)

    gen_target(target_files, context)

    return target_files


def yocto_common_create(machine, target, scripts_path, layer_output_dir, codedump, properties_file, properties_str="", expand_common=True):
    """
    Common layer-creation code

    machine - user-defined machine name (if needed, will generate 'machine' var)
    target - the 'target' the layer will be based on, must be one in
           scripts/lib/bsp/substrate/target/arch
    scripts_path - absolute path to yocto /scripts dir
    layer_output_dir - dirname to create for layer
    codedump - dump generated code to bspgen.out
    properties_file - use values from this file if nonempty i.e no prompting
    properties_str - use values from this string if nonempty i.e no prompting
    expand_common - boolean, use the contents of (for bsp layers) arch/common
    """
    if os.path.exists(layer_output_dir):
        print("\nlayer output dir already exists, exiting. (%s)" % layer_output_dir)
        sys.exit(1)

    properties = None

    if properties_file:
        try:
            infile = open(properties_file, "r")
            properties = json.load(infile)
        except IOError:
            print("Couldn't open properties file %s for reading, exiting" % properties_file)
            sys.exit(1)
        except ValueError:
            print("Wrong format on properties file %s, exiting" % properties_file)
            sys.exit(1)

    if properties_str and not properties:
        properties = json.loads(properties_str)

    os.mkdir(layer_output_dir)

    context = create_context(machine, target, scripts_path)
    target_files = expand_targets(context, layer_output_dir, expand_common)

    input_lines = gather_inputlines(target_files)

    program_lines = []

    gen_program_header_lines(program_lines)

    gen_initial_property_vals(input_lines, program_lines)

    if properties:
        gen_supplied_property_vals(properties, program_lines)

    gen_program_machine_lines(machine, program_lines)

    if not properties:
        gen_program_input_lines(input_lines, program_lines, context)

    gen_program_lines(target_files, program_lines)

    run_program_lines(program_lines, codedump)


def yocto_layer_create(layer_name, scripts_path, layer_output_dir, codedump, properties_file, properties=""):
    """
    Create yocto layer

    layer_name - user-defined layer name
    scripts_path - absolute path to yocto /scripts dir
    layer_output_dir - dirname to create for layer
    codedump - dump generated code to bspgen.out
    properties_file - use values from this file if nonempty i.e no prompting
    properties - use values from this string if nonempty i.e no prompting
    """
    yocto_common_create(layer_name, "layer", scripts_path, layer_output_dir, codedump, properties_file, properties, False)

    print("\nNew layer created in %s.\n" % layer_output_dir)
    print("Don't forget to add it to your BBLAYERS (for details see %s/README)." % layer_output_dir)


def yocto_bsp_create(machine, arch, scripts_path, bsp_output_dir, codedump, properties_file, properties=None):
    """
    Create bsp

    machine - user-defined machine name
    arch - the arch the bsp will be based on, must be one in
           scripts/lib/bsp/substrate/target/arch
    scripts_path - absolute path to yocto /scripts dir
    bsp_output_dir - dirname to create for BSP
    codedump - dump generated code to bspgen.out
    properties_file - use values from this file if nonempty i.e no prompting
    properties - use values from this string if nonempty i.e no prompting
    """
    yocto_common_create(machine, arch, scripts_path, bsp_output_dir, codedump, properties_file, properties)

    print("\nNew %s BSP created in %s" % (arch, bsp_output_dir))


def print_dict(items, indent = 0):
    """
    Print the values in a possibly nested dictionary.
    """
    for key, val in items.items():
        print("    "*indent + "\"%s\" :" % key)
        if type(val) == dict:
            print("{")
            print_dict(val, indent + 1)
            print("    "*indent + "}")
        else:
            print("%s" % val)


def get_properties(input_lines):
    """
    Get the complete set of properties for all the input items in the
    BSP, as a possibly nested dictionary.
    """
    properties = {}

    for line in input_lines:
        if isinstance(line, InputLineGroup):
            statement = line.group[0].line
            group_properties = get_properties(line.group)
            properties[statement] = group_properties
            continue

        if not isinstance(line, InputLine):
            continue

        if isinstance(line, ChoiceInputLine):
            continue

        props = line.props
        item = {}
        name = props["name"]
        for key, val in props.items():
            if not key == "name":
                item[key] = val
        properties[name] = item

    return properties


def yocto_layer_list_properties(arch, scripts_path, properties_file, expand_common=True):
    """
    List the complete set of properties for all the input items in the
    layer.  If properties_file is non-null, write the complete set of
    properties as a nested JSON object corresponding to a possibly
    nested dictionary.
    """
    context = create_context("unused", arch, scripts_path)
    target_files = expand_targets(context, "unused", expand_common)

    input_lines = gather_inputlines(target_files)

    properties = get_properties(input_lines)
    if properties_file:
        try:
            of = open(properties_file, "w")
        except IOError:
            print("Couldn't open properties file %s for writing, exiting" % properties_file)
            sys.exit(1)

        json.dump(properties, of, indent=1)
    else:
        print_dict(properties)


def split_nested_property(property):
    """
    A property name of the form x.y describes a nested property
    i.e. the property y is contained within x and can be addressed
    using standard JSON syntax for nested properties.  Note that if a
    property name itself contains '.', it should be contained in
    double quotes.
    """
    splittable_property = ""
    in_quotes = False
    for c in property:
        if c == '.' and not in_quotes:
            splittable_property += '\n'
            continue
        if c == '"':
            in_quotes = not in_quotes
        splittable_property += c

    split_properties = splittable_property.split('\n')

    if len(split_properties) > 1:
        return split_properties

    return None


def find_input_line_group(substring, input_lines):
    """
    Find and return the InputLineGroup containing the specified substring.
    """
    for line in input_lines:
        if isinstance(line, InputLineGroup):
            if substring in line.group[0].line:
                return line

    return None


def find_input_line(name, input_lines):
    """
    Find the input line with the specified name.
    """
    for line in input_lines:
        if isinstance(line, InputLineGroup):
            l = find_input_line(name, line.group)
            if l:
                return l

        if isinstance(line, InputLine):
            try:
                if line.props["name"] == name:
                    return line
                if line.props["name"] + "_" + line.props["nameappend"] == name:
                    return line
            except KeyError:
                pass

    return None


def print_values(type, values_list):
    """
    Print the values in the given list of values.
    """
    if type == "choicelist":
        for value in values_list:
            print("[\"%s\", \"%s\"]" % (value[0], value[1]))
    elif type == "boolean":
        for value in values_list:
            print("[\"%s\", \"%s\"]" % (value[0], value[1]))


def yocto_layer_list_property_values(arch, property, scripts_path, properties_file, expand_common=True):
    """
    List the possible values for a given input property.  If
    properties_file is non-null, write the complete set of properties
    as a JSON object corresponding to an array of possible values.
    """
    context = create_context("unused", arch, scripts_path)
    context["name"] = property

    target_files = expand_targets(context, "unused", expand_common)

    input_lines = gather_inputlines(target_files)

    properties = get_properties(input_lines)

    nested_properties = split_nested_property(property)
    if nested_properties:
        # currently the outer property of a nested property always
        # corresponds to an input line group
        input_line_group = find_input_line_group(nested_properties[0], input_lines)
        if input_line_group:
            input_lines[:] = input_line_group.group[1:]
            # The inner property of a nested property name is the
            # actual property name we want, so reset to that
            property = nested_properties[1]

    input_line = find_input_line(property, input_lines)
    if not input_line:
        print("Couldn't find values for property %s" % property)
        return

    values_list = []

    type = input_line.props["type"]
    if type == "boolean":
        values_list.append(["y", "n"])
    elif type == "choicelist" or type == "checklist":
        try:
            gen_fn = input_line.props["gen"]
            if nested_properties:
                context["filename"] = nested_properties[0]
                try:
                    context["branches_base"] = input_line.props["branches_base"]
                except KeyError:
                    context["branches_base"] = None
            values_list = input_line.gen_choices_list(context, False)
        except KeyError:
            for choice in input_line.choices:
                choicepair = []
                choicepair.append(choice.val)
                choicepair.append(choice.desc)
                values_list.append(choicepair)

    if properties_file:
        try:
            of = open(properties_file, "w")
        except IOError:
            print("Couldn't open properties file %s for writing, exiting" % properties_file)
            sys.exit(1)

        json.dump(values_list, of)

    print_values(type, values_list)


def yocto_bsp_list(args, scripts_path):
    """
    Print available architectures, or the complete list of properties
    defined by the BSP, or the possible values for a particular BSP
    property.
    """
    if args.karch == "karch":
        lib_path = scripts_path + '/lib'
        bsp_path = lib_path + '/bsp'
        arch_path = bsp_path + '/substrate/target/arch'
        print("Architectures available:")
        for arch in os.listdir(arch_path):
            if arch == "common" or arch == "layer":
                continue
            print("    %s" % arch)
        return

    if args.properties:
        yocto_layer_list_properties(args.karch, scripts_path, args.properties_file)
    elif args.property:
        yocto_layer_list_property_values(args.karch, args.property, scripts_path, args.properties_file)



def yocto_layer_list(args, scripts_path, properties_file):
    """
    Print the complete list of input properties defined by the layer,
    or the possible values for a particular layer property.
    """
    if len(args) < 1:
        return False

    if len(args) < 1 or len(args) > 2:
        return False

    if len(args) == 1:
        if args[0] == "properties":
            yocto_layer_list_properties("layer", scripts_path, properties_file, False)
        else:
            return False

    if len(args) == 2:
        if args[0] == "property":
            yocto_layer_list_property_values("layer", args[1], scripts_path, properties_file, False)
        else:
            return False

    return True


def map_standard_kbranch(need_new_kbranch, new_kbranch, existing_kbranch):
    """
    Return the linux-yocto bsp branch to use with the specified
    kbranch.  This handles the -standard variants for 3.4 and 3.8; the
    other variants don't need mappings.
    """
    if need_new_kbranch == "y":
        kbranch = new_kbranch
    else:
        kbranch = existing_kbranch

    if kbranch.startswith("standard/common-pc-64"):
        return "bsp/common-pc-64/common-pc-64-standard.scc"
    if kbranch.startswith("standard/common-pc"):
        return "bsp/common-pc/common-pc-standard.scc"
    else:
        return "ktypes/standard/standard.scc"


def map_preempt_rt_kbranch(need_new_kbranch, new_kbranch, existing_kbranch):
    """
    Return the linux-yocto bsp branch to use with the specified
    kbranch.  This handles the -preempt-rt variants for 3.4 and 3.8;
    the other variants don't need mappings.
    """
    if need_new_kbranch == "y":
        kbranch = new_kbranch
    else:
        kbranch = existing_kbranch

    if kbranch.startswith("standard/preempt-rt/common-pc-64"):
        return "bsp/common-pc-64/common-pc-64-preempt-rt.scc"
    if kbranch.startswith("standard/preempt-rt/common-pc"):
        return "bsp/common-pc/common-pc-preempt-rt.scc"
    else:
        return "ktypes/preempt-rt/preempt-rt.scc"


def map_tiny_kbranch(need_new_kbranch, new_kbranch, existing_kbranch):
    """
    Return the linux-yocto bsp branch to use with the specified
    kbranch.  This handles the -tiny variants for 3.4 and 3.8; the
    other variants don't need mappings.
    """
    if need_new_kbranch == "y":
        kbranch = new_kbranch
    else:
        kbranch = existing_kbranch

    if kbranch.startswith("standard/tiny/common-pc"):
        return "bsp/common-pc/common-pc-tiny.scc"
    else:
        return "ktypes/tiny/tiny.scc"
