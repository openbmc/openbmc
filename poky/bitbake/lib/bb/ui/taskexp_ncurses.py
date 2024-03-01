#
# BitBake Graphical ncurses-based Dependency Explorer
#   * Based on the GTK implementation
#   * Intended to run on any Linux host
#
# Copyright (C) 2007        Ross Burton
# Copyright (C) 2007 - 2008 Richard Purdie
# Copyright (C) 2022 - 2024 David Reyna
#
# SPDX-License-Identifier: GPL-2.0-only
#

#
# Execution example:
#   $ bitbake -g -u taskexp_ncurses zlib acl
#
# Self-test example (executes a script of GUI actions):
#   $ TASK_EXP_UNIT_TEST=1 bitbake -g -u taskexp_ncurses zlib acl
#   ...
#   $ echo $?
#   0
#   $ TASK_EXP_UNIT_TEST=1 bitbake -g -u taskexp_ncurses zlib acl foo
#   ERROR: Nothing PROVIDES 'foo'. Close matches:
#   ofono
#   $ echo $?
#   1
#
# Self-test with no terminal example (only tests dependency fetch from bitbake):
#   $ TASK_EXP_UNIT_TEST_NOTERM=1 bitbake -g -u taskexp_ncurses quilt
#   $ echo $?
#   0
#
# Features:
# * Ncurses is used for the presentation layer. Only the 'curses'
#   library is used (none of the extension libraries), plus only
#   one main screen is used (no sub-windows)
# * Uses the 'generateDepTreeEvent' bitbake event to fetch the
#   dynamic dependency data based on passed recipes
# * Computes and provides reverse dependencies
# * Supports task sorting on:
#   (a) Task dependency order within each recipe
#   (b) Pure alphabetical order
#   (c) Provisions for third sort order (bitbake order?)
# * The 'Filter' does a "*string*" wildcard filter on tasks in the
#   main window, dynamically re-ordering and re-centering the content
# * A 'Print' function exports the selected task or its whole recipe
#   task set to the default file "taskdep.txt"
# * Supports a progress bar for bitbake loads and file printing
# * Line art for box drawing supported, ASCII art an alernative
# * No horizontal scrolling support. Selected task's full name
#   shown in bottom bar
# * Dynamically catches terminals that are (or become) too small
# * Exception to insure return to normal terminal on errors
# * Debugging support, self test option
#

import sys
import traceback
import curses
import re
import time

# Bitbake server support
import threading
from xmlrpc import client
import bb
import bb.event

# Dependency indexes (depends_model)
(TYPE_DEP, TYPE_RDEP) = (0, 1)
DEPENDS_TYPE = 0
DEPENDS_TASK = 1
DEPENDS_DEPS = 2
# Task indexes (task_list)
TASK_NAME = 0
TASK_PRIMARY = 1
TASK_SORT_ALPHA = 2
TASK_SORT_DEPS = 3
TASK_SORT_BITBAKE = 4
# Sort options (default is SORT_DEPS)
SORT_ALPHA = 0
SORT_DEPS = 1
SORT_BITBAKE_ENABLE = False # NOTE: future sort
SORT_BITBAKE = 2
sort_model = SORT_DEPS
# Print options
PRINT_MODEL_1 = 0
PRINT_MODEL_2 = 1
print_model = PRINT_MODEL_2
print_file_name = "taskdep_print.log"
print_file_backup_name = "taskdep_print_backup.log"
is_printed = False
is_filter = False

# Standard (and backup) key mappings
CHAR_NUL = 0            # Used as self-test nop char
CHAR_BS_H = 8           # Alternate backspace key
CHAR_TAB = 9
CHAR_RETURN = 10
CHAR_ESCAPE = 27
CHAR_UP = ord('{')      # Used as self-test ASCII char
CHAR_DOWN = ord('}')    # Used as self-test ASCII char

# Color_pair IDs
CURSES_NORMAL = 0
CURSES_HIGHLIGHT = 1
CURSES_WARNING = 2


#################################################
### Debugging support
###

verbose = False

# Debug: message display slow-step through display update issues
def alert(msg,screen):
    if msg:
        screen.addstr(0, 10, '[%-4s]' % msg)
        screen.refresh();
        curses.napms(2000)
    else:
        if do_line_art:
            for i in range(10, 24):
                screen.addch(0, i, curses.ACS_HLINE)
        else:
            screen.addstr(0, 10, '-' * 14)
        screen.refresh();

# Debug: display edge conditions on frame movements
def debug_frame(nbox_ojb):
    if verbose:
        nbox_ojb.screen.addstr(0, 50, '[I=%2d,O=%2d,S=%3s,H=%2d,M=%4d]' % (
            nbox_ojb.cursor_index,
            nbox_ojb.cursor_offset,
            nbox_ojb.scroll_offset,
            nbox_ojb.inside_height,
            len(nbox_ojb.task_list),
        ))
        nbox_ojb.screen.refresh();

#
# Unit test (assumes that 'quilt-native' is always present)
#

unit_test = os.environ.get('TASK_EXP_UNIT_TEST')
unit_test_cmnds=[
    '# Default selected task in primary box',
    'tst_selected=<TASK>.do_recipe_qa',
    '# Default selected task in deps',
    'tst_entry=<TAB>',
    'tst_selected=',
    '# Default selected task in rdeps',
    'tst_entry=<TAB>',
    'tst_selected=<TASK>.do_fetch',
    "# Test 'select' back to primary box",
    'tst_entry=<CR>',
    '#tst_entry=<DOWN>',  # optional injected error
    'tst_selected=<TASK>.do_fetch',
    '# Check filter',
    'tst_entry=/uilt-nativ/',
    'tst_selected=quilt-native.do_recipe_qa',
    '# Check print',
    'tst_entry=p',
    'tst_printed=quilt-native.do_fetch',
    '#tst_printed=quilt-foo.do_nothing',  # optional injected error
    '# Done!',
    'tst_entry=q',
]
unit_test_idx=0
unit_test_command_chars=''
unit_test_results=[]
def unit_test_action(active_package):
    global unit_test_idx
    global unit_test_command_chars
    global unit_test_results
    ret = CHAR_NUL
    if unit_test_command_chars:
        ch = unit_test_command_chars[0]
        unit_test_command_chars = unit_test_command_chars[1:]
        time.sleep(0.5)
        ret = ord(ch)
    else:
        line = unit_test_cmnds[unit_test_idx]
        unit_test_idx += 1
        line = re.sub('#.*', '', line).strip()
        line = line.replace('<TASK>',active_package.primary[0])
        line = line.replace('<TAB>','\t').replace('<CR>','\n')
        line = line.replace('<UP>','{').replace('<DOWN>','}')
        if not line: line = 'nop=nop'
        cmnd,value = line.split('=')
        if cmnd == 'tst_entry':
            unit_test_command_chars = value
        elif cmnd == 'tst_selected':
            active_selected = active_package.get_selected()
            if active_selected != value:
                unit_test_results.append("ERROR:SELFTEST:expected '%s' but got '%s' (NOTE:bitbake may have changed)" % (value,active_selected))
                ret = ord('Q')
            else:
                unit_test_results.append("Pass:SELFTEST:found '%s'" % (value))
        elif cmnd == 'tst_printed':
            result = os.system('grep %s %s' % (value,print_file_name))
            if result:
                unit_test_results.append("ERROR:PRINTTEST:expected '%s' in '%s'" % (value,print_file_name))
                ret = ord('Q')
            else:
                unit_test_results.append("Pass:PRINTTEST:found '%s'" % (value))
    # Return the action (CHAR_NUL for no action til next round)
    return(ret)

# Unit test without an interative terminal (e.g. ptest)
unit_test_noterm = os.environ.get('TASK_EXP_UNIT_TEST_NOTERM')


#################################################
### Window frame rendering
###
### By default, use the normal line art. Since
###  these extended characters are not ASCII, one
###  must use the ncursus API to render them
### The alternate ASCII line art set is optionally
###  available via the 'do_line_art' flag

# By default, render frames using line art
do_line_art = True

# ASCII render set option
CHAR_HBAR = '-'
CHAR_VBAR = '|'
CHAR_UL_CORNER = '/'
CHAR_UR_CORNER = '\\'
CHAR_LL_CORNER = '\\'
CHAR_LR_CORNER = '/'

# Box frame drawing with line-art
def line_art_frame(box):
    x = box.base_x
    y = box.base_y
    w = box.width
    h = box.height + 1

    if do_line_art:
        for i in range(1, w - 1):
            box.screen.addch(y, x + i, curses.ACS_HLINE, box.color)
            box.screen.addch(y + h - 1, x + i, curses.ACS_HLINE, box.color)
        body_line = "%s" % (' ' * (w - 2))
        for i in range(1, h - 1):
            box.screen.addch(y + i, x, curses.ACS_VLINE, box.color)
            box.screen.addstr(y + i, x + 1, body_line, box.color)
            box.screen.addch(y + i, x + w - 1, curses.ACS_VLINE, box.color)
        box.screen.addch(y, x, curses.ACS_ULCORNER, box.color)
        box.screen.addch(y, x + w - 1, curses.ACS_URCORNER, box.color)
        box.screen.addch(y + h - 1, x, curses.ACS_LLCORNER, box.color)
        box.screen.addch(y + h - 1, x + w - 1, curses.ACS_LRCORNER, box.color)
    else:
        top_line  = "%s%s%s" % (CHAR_UL_CORNER,CHAR_HBAR * (w - 2),CHAR_UR_CORNER)
        body_line = "%s%s%s" % (CHAR_VBAR,' ' * (w - 2),CHAR_VBAR)
        bot_line  = "%s%s%s" % (CHAR_UR_CORNER,CHAR_HBAR * (w - 2),CHAR_UL_CORNER)
        tag_line  = "%s%s%s" % ('[',CHAR_HBAR * (w - 2),']')
        # Top bar
        box.screen.addstr(y, x, top_line)
        # Middle frame
        for i in range(1, (h - 1)):
            box.screen.addstr(y+i, x, body_line)
        # Bottom bar
        box.screen.addstr(y + (h - 1), x, bot_line)

# Connect the separate boxes
def line_art_fixup(box):
    if do_line_art:
        box.screen.addch(box.base_y+2, box.base_x, curses.ACS_LTEE, box.color)
        box.screen.addch(box.base_y+2, box.base_x+box.width-1, curses.ACS_RTEE, box.color)


#################################################
### Ncurses box object : box frame object to display
### and manage a sub-window's display elements
### using basic ncurses
###
### Supports:
###   * Frame drawing, content (re)drawing
###   * Content scrolling via ArrowUp, ArrowDn, PgUp, PgDN,
###   * Highlighting for active selected item
###   * Content sorting based on selected sort model
###

class NBox():
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        # Box description
        self.screen = screen
        self.label = label
        self.primary = primary
        self.color = curses.color_pair(CURSES_NORMAL) if screen else None
        # Box boundaries
        self.base_x = base_x
        self.base_y = base_y
        self.width = width
        self.height = height
        # Cursor/scroll management
        self.cursor_enable = False
        self.cursor_index = 0   # Absolute offset
        self.cursor_offset = 0  # Frame centric offset
        self.scroll_offset = 0  # Frame centric offset
        # Box specific content
        # Format of each entry is [package_name,is_primary_recipe,alpha_sort_key,deps_sort_key]
        self.task_list = []

    @property
    def inside_width(self):
        return(self.width-2)

    @property
    def inside_height(self):
        return(self.height-2)

    # Populate the box's content, include the sort mappings and is_primary flag
    def task_list_append(self,task_name,dep):
        task_sort_alpha = task_name
        task_sort_deps = dep.get_dep_sort(task_name)
        is_primary = False
        for primary in self.primary:
            if task_name.startswith(primary+'.'):
                is_primary = True
        if SORT_BITBAKE_ENABLE:
            task_sort_bitbake = dep.get_bb_sort(task_name)
            self.task_list.append([task_name,is_primary,task_sort_alpha,task_sort_deps,task_sort_bitbake])
        else:
            self.task_list.append([task_name,is_primary,task_sort_alpha,task_sort_deps])

    def reset(self):
        self.task_list = []
        self.cursor_index = 0   # Absolute offset
        self.cursor_offset = 0  # Frame centric offset
        self.scroll_offset = 0  # Frame centric offset

    # Sort the box's content based on the current sort model
    def sort(self):
        if SORT_ALPHA == sort_model:
            self.task_list.sort(key = lambda x: x[TASK_SORT_ALPHA])
        elif SORT_DEPS == sort_model:
            self.task_list.sort(key = lambda x: x[TASK_SORT_DEPS])
        elif SORT_BITBAKE == sort_model:
            self.task_list.sort(key = lambda x: x[TASK_SORT_BITBAKE])

    # The target package list (to hightlight), from the command line
    def set_primary(self,primary):
        self.primary = primary

    # Draw the box's outside frame
    def draw_frame(self):
        line_art_frame(self)
        # Title
        self.screen.addstr(self.base_y,
            (self.base_x + (self.width//2))-((len(self.label)+2)//2),
            '['+self.label+']')
        self.screen.refresh()

    # Draw the box's inside text content
    def redraw(self):
        task_list_len = len(self.task_list)
        # Middle frame
        body_line = "%s" % (' ' * (self.inside_width-1) )
        for i in range(0,self.inside_height+1):
            if i < (task_list_len + self.scroll_offset):
                str_ctl = "%%-%ss" % (self.width-3)
                # Safety assert
                if (i + self.scroll_offset) >= task_list_len:
                    alert("REDRAW:%2d,%4d,%4d" % (i,self.scroll_offset,task_list_len),self.screen)
                    break

                task_obj = self.task_list[i + self.scroll_offset]
                task = task_obj[TASK_NAME][:self.inside_width-1]
                task_primary = task_obj[TASK_PRIMARY]

                if task_primary:
                    line = str_ctl % task[:self.inside_width-1]
                    self.screen.addstr(self.base_y+1+i, self.base_x+2, line, curses.A_BOLD)
                else:
                    line = str_ctl % task[:self.inside_width-1]
                    self.screen.addstr(self.base_y+1+i, self.base_x+2, line)
            else:
                line = "%s" % (' ' * (self.inside_width-1) )
                self.screen.addstr(self.base_y+1+i, self.base_x+2, line)
        self.screen.refresh()

    # Show the current selected task over the bottom of the frame
    def show_selected(self,selected_task):
        if not selected_task:
            selected_task = self.get_selected()
        tag_line   = "%s%s%s" % ('[',CHAR_HBAR * (self.width-2),']')
        self.screen.addstr(self.base_y + self.height, self.base_x, tag_line)
        self.screen.addstr(self.base_y + self.height,
            (self.base_x + (self.width//2))-((len(selected_task)+2)//2),
            '['+selected_task+']')
        self.screen.refresh()

    # Load box with new table of content
    def update_content(self,task_list):
        self.task_list = task_list
        if self.cursor_enable:
            cursor_update(turn_on=False)
        self.cursor_index = 0
        self.cursor_offset = 0
        self.scroll_offset = 0
        self.redraw()
        if self.cursor_enable:
            cursor_update(turn_on=True)

    # Manage the box's highlighted task and blinking cursor character
    def cursor_on(self,is_on):
        self.cursor_enable = is_on
        self.cursor_update(is_on)

    # High-light the current pointed package, normal for released packages
    def cursor_update(self,turn_on=True):
        str_ctl = "%%-%ss" % (self.inside_width-1)
        try:
            if len(self.task_list):
                task_obj = self.task_list[self.cursor_index]
                task = task_obj[TASK_NAME][:self.inside_width-1]
                task_primary = task_obj[TASK_PRIMARY]
                task_font = curses.A_BOLD if task_primary else 0
            else:
                task = ''
                task_font = 0
        except Exception as e:
            alert("CURSOR_UPDATE:%s" % (e),self.screen)
            return
        if turn_on:
            self.screen.addstr(self.base_y+1+self.cursor_offset,self.base_x+1,">", curses.color_pair(CURSES_HIGHLIGHT) | curses.A_BLINK)
            self.screen.addstr(self.base_y+1+self.cursor_offset,self.base_x+2,str_ctl % task, curses.color_pair(CURSES_HIGHLIGHT) | task_font)
        else:
            self.screen.addstr(self.base_y+1+self.cursor_offset,self.base_x+1," ")
            self.screen.addstr(self.base_y+1+self.cursor_offset,self.base_x+2,str_ctl % task, task_font)

    # Down arrow
    def line_down(self):
        if len(self.task_list) <= (self.cursor_index+1):
            return
        self.cursor_update(turn_on=False)
        self.cursor_index += 1
        self.cursor_offset += 1
        if self.cursor_offset > (self.inside_height):
            self.cursor_offset -= 1
            self.scroll_offset += 1
            self.redraw()
        self.cursor_update(turn_on=True)
        debug_frame(self)

    # Up arrow
    def line_up(self):
        if 0 > (self.cursor_index-1):
            return
        self.cursor_update(turn_on=False)
        self.cursor_index -= 1
        self.cursor_offset -= 1
        if self.cursor_offset < 0:
            self.cursor_offset += 1
            self.scroll_offset -= 1
            self.redraw()
        self.cursor_update(turn_on=True)
        debug_frame(self)

    # Page down
    def page_down(self):
        max_task = len(self.task_list)-1
        if max_task < self.inside_height:
            return
        self.cursor_update(turn_on=False)
        self.cursor_index += 10
        self.cursor_index = min(self.cursor_index,max_task)
        self.cursor_offset = min(self.inside_height,self.cursor_index)
        self.scroll_offset = self.cursor_index - self.cursor_offset
        self.redraw()
        self.cursor_update(turn_on=True)
        debug_frame(self)

    # Page up
    def page_up(self):
        max_task = len(self.task_list)-1
        if max_task < self.inside_height:
            return
        self.cursor_update(turn_on=False)
        self.cursor_index -= 10
        self.cursor_index = max(self.cursor_index,0)
        self.cursor_offset = max(0, self.inside_height - (max_task - self.cursor_index))
        self.scroll_offset = self.cursor_index - self.cursor_offset
        self.redraw()
        self.cursor_update(turn_on=True)
        debug_frame(self)

    # Return the currently selected task name for this box
    def get_selected(self):
        if self.task_list:
            return(self.task_list[self.cursor_index][TASK_NAME])
        else:
            return('')

#################################################
### The helper sub-windows
###

# Show persistent help at the top of the screen
class HelpBarView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(HelpBarView, self).__init__(screen, label, primary, base_x, base_y, width, height)

    def show_help(self,show):
        self.screen.addstr(self.base_y,self.base_x, "%s" % (' ' * self.inside_width))
        if show:
            help = "Help='?' Filter='/' NextBox=<Tab> Select=<Enter> Print='p','P' Quit='q'"
            bar_size = self.inside_width - 5 - len(help)
            self.screen.addstr(self.base_y,self.base_x+((self.inside_width-len(help))//2), help)
        self.screen.refresh()

# Pop up a detailed Help box
class HelpBoxView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height, dep):
        super(HelpBoxView, self).__init__(screen, label, primary, base_x, base_y, width, height)
        self.x_pos = 0
        self.y_pos = 0
        self.dep = dep

    # Instantial the pop-up help box
    def show_help(self,show):
        self.x_pos = self.base_x + 4
        self.y_pos = self.base_y + 2

        def add_line(line):
            if line:
                self.screen.addstr(self.y_pos,self.x_pos,line)
            self.y_pos += 1

        # Gather some statisics
        dep_count = 0
        rdep_count = 0
        for task_obj in self.dep.depends_model:
            if TYPE_DEP == task_obj[DEPENDS_TYPE]:
                dep_count += 1
            elif TYPE_RDEP == task_obj[DEPENDS_TYPE]:
                rdep_count += 1

        self.draw_frame()
        line_art_fixup(self.dep)
        add_line("Quit                : 'q' ")
        add_line("Filter task names   : '/'")
        add_line("Tab to next box     : <Tab>")
        add_line("Select a task       : <Enter>")
        add_line("Print task's deps   : 'p'")
        add_line("Print recipe's deps : 'P'")
        add_line(" -> '%s'" % print_file_name)
        add_line("Sort toggle         : 's'")
        add_line(" %s Recipe inner-depends order" % ('->' if (SORT_DEPS == sort_model) else '- '))
        add_line(" %s Alpha-numeric order" % ('->' if (SORT_ALPHA == sort_model) else '- '))
        if SORT_BITBAKE_ENABLE:
            add_line(" %s Bitbake order" % ('->' if (TASK_SORT_BITBAKE == sort_model) else '- '))
        add_line("Alternate backspace : <CTRL-H>")
        add_line("")
        add_line("Primary recipes = %s"  % ','.join(self.primary))
        add_line("Task  count     = %4d" % len(self.dep.pkg_model))
        add_line("Deps  count     = %4d" % dep_count)
        add_line("RDeps count     = %4d" % rdep_count)
        add_line("")
        self.screen.addstr(self.y_pos,self.x_pos+7,"<Press any key>", curses.color_pair(CURSES_HIGHLIGHT))
        self.screen.refresh()
        c = self.screen.getch()

# Show a progress bar
class ProgressView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(ProgressView, self).__init__(screen, label, primary, base_x, base_y, width, height)

    def progress(self,title,current,max):
        if title:
            self.label = title
        else:
            title = self.label
        if max <=0: max = 10
        bar_size = self.width - 7 - len(title)
        bar_done = int( (float(current)/float(max)) * float(bar_size) )
        self.screen.addstr(self.base_y,self.base_x, " %s:[%s%s]" % (title,'*' * bar_done,' ' * (bar_size-bar_done)))
        self.screen.refresh()
        return(current+1)

    def clear(self):
        self.screen.addstr(self.base_y,self.base_x, "%s" % (' ' * self.width))
        self.screen.refresh()

# Implement a task filter bar
class FilterView(NBox):
    SEARCH_NOP = 0
    SEARCH_GO = 1
    SEARCH_CANCEL = 2

    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(FilterView, self).__init__(screen, label, primary, base_x, base_y, width, height)
        self.do_show = False
        self.filter_str = ""

    def clear(self,enable_show=True):
        self.filter_str = ""

    def show(self,enable_show=True):
        self.do_show = enable_show
        if self.do_show:
            self.screen.addstr(self.base_y,self.base_x, "[ Filter: %-25s ]      '/'=cancel, format='abc'       " % self.filter_str[0:25])
        else:
            self.screen.addstr(self.base_y,self.base_x, "%s" % (' ' * self.width))
        self.screen.refresh()

    def show_prompt(self):
        self.screen.addstr(self.base_y,self.base_x + 10 + len(self.filter_str), " ")
        self.screen.addstr(self.base_y,self.base_x + 10 + len(self.filter_str), "")

    # Keys specific to the filter box (start/stop filter keys are in the main loop)
    def input(self,c,ch):
        ret = self.SEARCH_GO
        if c in (curses.KEY_BACKSPACE,CHAR_BS_H):
            #  Backspace
            if self.filter_str:
                self.filter_str = self.filter_str[0:-1]
                self.show()
        elif ((ch >= 'a') and (ch <= 'z')) or ((ch >= 'A') and (ch <= 'Z')) or ((ch >= '0') and (ch <= '9')) or (ch in (' ','_','.','-')):
            # The isalnum() acts strangly with keypad(True), so explicit bounds
            self.filter_str += ch
            self.show()
        else:
            ret = self.SEARCH_NOP
        return(ret)


#################################################
### The primary dependency windows
###

# The main list of package tasks
class PackageView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(PackageView, self).__init__(screen, label, primary, base_x, base_y, width, height)

    # Find and verticaly center a selected task (from filter or from dependent box)
    # The 'task_filter_str' can be a full or a partial (filter) task name
    def find(self,task_filter_str):
        found = False
        max = self.height-2
        if not task_filter_str:
            return(found)
        for i,task_obj in enumerate(self.task_list):
            task = task_obj[TASK_NAME]
            if task.startswith(task_filter_str):
                self.cursor_on(False)
                self.cursor_index = i

                # Position selected at vertical center
                vcenter = self.inside_height // 2
                if self.cursor_index <= vcenter:
                    self.scroll_offset = 0
                    self.cursor_offset = self.cursor_index
                elif self.cursor_index >= (len(self.task_list) - vcenter - 1):
                    self.cursor_offset = self.inside_height-1
                    self.scroll_offset = self.cursor_index - self.cursor_offset
                else:
                    self.cursor_offset = vcenter
                    self.scroll_offset = self.cursor_index - self.cursor_offset

                self.redraw()
                self.cursor_on(True)
                found = True
                break
        return(found)

# The view of dependent packages
class PackageDepView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(PackageDepView, self).__init__(screen, label, primary, base_x, base_y, width, height)

# The view of reverse-dependent packages
class PackageReverseDepView(NBox):
    def __init__(self, screen, label, primary, base_x, base_y, width, height):
        super(PackageReverseDepView, self).__init__(screen, label, primary, base_x, base_y, width, height)


#################################################
### DepExplorer : The parent frame and object
###

class DepExplorer(NBox):
    def __init__(self,screen):
        title = "Task Dependency Explorer"
        super(DepExplorer, self).__init__(screen, 'Task Dependency Explorer','',0,0,80,23)

        self.screen = screen
        self.pkg_model = []
        self.depends_model = []
        self.dep_sort_map = {}
        self.bb_sort_map = {}
        self.filter_str = ''
        self.filter_prev = 'deadbeef'

        if self.screen:
            self.help_bar_view = HelpBarView(screen, "Help",'',1,1,79,1)
            self.help_box_view = HelpBoxView(screen, "Help",'',0,2,40,20,self)
            self.progress_view = ProgressView(screen, "Progress",'',2,1,76,1)
            self.filter_view = FilterView(screen, "Filter",'',2,1,76,1)
            self.package_view = PackageView(screen, "Package",'alpha', 0,2,40,20)
            self.dep_view = PackageDepView(screen, "Dependencies",'beta',40,2,40,10)
            self.reverse_view = PackageReverseDepView(screen, "Dependent Tasks",'gamma',40,13,40,9)
            self.draw_frames()

    # Draw this main window's frame and all sub-windows
    def draw_frames(self):
        self.draw_frame()
        self.package_view.draw_frame()
        self.dep_view.draw_frame()
        self.reverse_view.draw_frame()
        if is_filter:
            self.filter_view.show(True)
            self.filter_view.show_prompt()
        else:
            self.help_bar_view.show_help(True)
        self.package_view.redraw()
        self.dep_view.redraw()
        self.reverse_view.redraw()
        self.show_selected(self.package_view.get_selected())
        line_art_fixup(self)

    # Parse the bitbake dependency event object
    def parse(self, depgraph):
        for task in depgraph["tdepends"]:
            self.pkg_model.insert(0, task)
            for depend in depgraph["tdepends"][task]:
                self.depends_model.insert (0, (TYPE_DEP, task, depend))
                self.depends_model.insert (0, (TYPE_RDEP, depend, task))
        if self.screen:
            self.dep_sort_prep()

    # Prepare the dependency sort order keys
    # This method creates sort keys per recipe tasks in
    # the order of each recipe's internal dependecies
    # Method:
    #   Filter the tasks in dep order in dep_sort_map = {}
    #   (a) Find a task that has no dependecies
    #       Ignore non-recipe specific tasks
    #   (b) Add it to the sort mapping dict with
    #       key of "<task_group>_<order>"
    #   (c) Remove it as a dependency from the other tasks
    #   (d) Repeat till all tasks are mapped
    # Use placeholders to insure each sub-dict is instantiated
    def dep_sort_prep(self):
        self.progress_view.progress('DepSort',0,4)
        # Init the task base entries
        self.progress_view.progress('DepSort',1,4)
        dep_table = {}
        bb_index = 0
        for task in self.pkg_model:
            # First define the incoming bitbake sort order
            self.bb_sort_map[task] = "%04d" % (bb_index)
            bb_index += 1
            task_group = task[0:task.find('.')]
            if task_group not in dep_table:
                dep_table[task_group] = {}
                dep_table[task_group]['-'] = {}         # Placeholder
            if task not in dep_table[task_group]:
                dep_table[task_group][task] = {}
                dep_table[task_group][task]['-'] = {}   # Placeholder
        # Add the task dependecy entries
        self.progress_view.progress('DepSort',2,4)
        for task_obj in self.depends_model:
            if task_obj[DEPENDS_TYPE] != TYPE_DEP:
                continue
            task = task_obj[DEPENDS_TASK]
            task_dep = task_obj[DEPENDS_DEPS]
            task_group = task[0:task.find('.')]
            # Only track depends within same group
            if task_dep.startswith(task_group+'.'):
                dep_table[task_group][task][task_dep] = 1
        self.progress_view.progress('DepSort',3,4)
        for task_group in dep_table:
            dep_index = 0
            # Whittle down the tasks of each group
            this_pass = 1
            do_loop = True
            while (len(dep_table[task_group]) > 1) and do_loop:
                this_pass += 1
                is_change = False
                delete_list = []
                for task in dep_table[task_group]:
                    if '-' == task:
                        continue
                    if 1 == len(dep_table[task_group][task]):
                        is_change = True
                        # No more deps, so collect this task...
                        self.dep_sort_map[task] = "%s_%04d" % (task_group,dep_index)
                        dep_index += 1
                        # ... remove it from other lists as resolved ...
                        for dep_task in dep_table[task_group]:
                            if task in dep_table[task_group][dep_task]:
                                del dep_table[task_group][dep_task][task]
                        # ... and remove it from from the task group
                        delete_list.append(task)
                for task in delete_list:
                    del dep_table[task_group][task]
                if not is_change:
                    alert("ERROR:DEP_SIEVE_NO_CHANGE:%s" % task_group,self.screen)
                    do_loop = False
                    continue
        self.progress_view.progress('',4,4)
        self.progress_view.clear()
        self.help_bar_view.show_help(True)
        if len(self.dep_sort_map) != len(self.pkg_model):
            alert("ErrorDepSort:%d/%d" % (len(self.dep_sort_map),len(self.pkg_model)),self.screen)

    # Look up a dep sort order key
    def get_dep_sort(self,key):
        if key in self.dep_sort_map:
            return(self.dep_sort_map[key])
        else:
            return(key)

    # Look up a bitbake sort order key
    def get_bb_sort(self,key):
        if key in self.bb_sort_map:
            return(self.bb_sort_map[key])
        else:
            return(key)

    # Find the selected package in the main frame, update the dependency frames content accordingly
    def select(self, package_name, only_update_dependents=False):
        if not package_name:
            package_name = self.package_view.get_selected()
        # alert("SELECT:%s:" % package_name,self.screen)

        if self.filter_str != self.filter_prev:
            self.package_view.cursor_on(False)
            # Fill of the main package task list using new filter
            self.package_view.task_list = []
            for package in self.pkg_model:
                if self.filter_str:
                    if self.filter_str in package:
                        self.package_view.task_list_append(package,self)
                else:
                    self.package_view.task_list_append(package,self)
            self.package_view.sort()
            self.filter_prev = self.filter_str

            # Old position is lost, assert new position of previous task (if still filtered in)
            self.package_view.cursor_index = 0
            self.package_view.cursor_offset = 0
            self.package_view.scroll_offset = 0
            self.package_view.redraw()
            self.package_view.cursor_on(True)

        # Make sure the selected package is in view, with implicit redraw()
        if (not only_update_dependents):
            self.package_view.find(package_name)
        # In case selected name change (i.e. filter removed previous)
        package_name = self.package_view.get_selected()

        # Filter the package's dependent list to the dependent view
        self.dep_view.reset()
        for package_def in self.depends_model:
            if (package_def[DEPENDS_TYPE] == TYPE_DEP) and (package_def[DEPENDS_TASK] == package_name):
                self.dep_view.task_list_append(package_def[DEPENDS_DEPS],self)
        self.dep_view.sort()
        self.dep_view.redraw()
        # Filter the package's dependent list to the reverse dependent view
        self.reverse_view.reset()
        for package_def in self.depends_model:
            if (package_def[DEPENDS_TYPE] == TYPE_RDEP) and (package_def[DEPENDS_TASK] == package_name):
                self.reverse_view.task_list_append(package_def[DEPENDS_DEPS],self)
        self.reverse_view.sort()
        self.reverse_view.redraw()
        self.show_selected(package_name)
        self.screen.refresh()

    # The print-to-file method
    def print_deps(self,whole_group=False):
        global is_printed
        # Print the selected deptree(s) to a file
        if not is_printed:
            try:
                # Move to backup any exiting file before first write
                if os.path.isfile(print_file_name):
                    os.system('mv -f %s %s' % (print_file_name,print_file_backup_name))
            except Exception as e:
                alert(e,self.screen)
                alert('',self.screen)
        print_list = []
        selected_task = self.package_view.get_selected()
        if not selected_task:
            return
        if not whole_group:
            print_list.append(selected_task)
        else:
            # Use the presorted task_group order from 'package_view'
            task_group = selected_task[0:selected_task.find('.')+1]
            for task_obj in self.package_view.task_list:
                task = task_obj[TASK_NAME]
                if task.startswith(task_group):
                    print_list.append(task)
        with open(print_file_name, "a") as fd:
            print_max = len(print_list)
            print_count = 1
            self.progress_view.progress('Write "%s"' % print_file_name,0,print_max)
            for task in print_list:
                print_count = self.progress_view.progress('',print_count,print_max)
                self.select(task)
                self.screen.refresh();
                # Utilize the current print output model
                if print_model == PRINT_MODEL_1:
                    print("=== Dependendency Snapshot ===",file=fd)
                    print(" = Package =",file=fd)
                    print('   '+task,file=fd)
                    # Fill in the matching dependencies
                    print(" = Dependencies =",file=fd)
                    for task_obj in self.dep_view.task_list:
                        print('   '+ task_obj[TASK_NAME],file=fd)
                    print(" = Dependent Tasks =",file=fd)
                    for task_obj in self.reverse_view.task_list:
                        print('   '+ task_obj[TASK_NAME],file=fd)
                if print_model == PRINT_MODEL_2:
                    print("=== Dependendency Snapshot ===",file=fd)
                    dep_count = len(self.dep_view.task_list) - 1
                    for i,task_obj in enumerate(self.dep_view.task_list):
                        print('%s%s' % ("Dep    =" if (i==dep_count) else "        ",task_obj[TASK_NAME]),file=fd)
                    if not self.dep_view.task_list:
                        print('Dep    =',file=fd)
                    print("Package=%s" % task,file=fd)
                    for i,task_obj in enumerate(self.reverse_view.task_list):
                        print('%s%s' % ("RDep   =" if (i==0) else "        ",task_obj[TASK_NAME]),file=fd)
                    if not self.reverse_view.task_list:
                        print('RDep   =',file=fd)
                curses.napms(2000)
                self.progress_view.clear()
                self.help_bar_view.show_help(True)
            print('',file=fd)
        # Restore display to original selected task
        self.select(selected_task)
        is_printed = True

#################################################
### Load bitbake data
###

def bitbake_load(server, eventHandler, params, dep, curses_off, screen):
    global bar_len_old
    bar_len_old = 0

    # Support no screen
    def progress(msg,count,max):
        global bar_len_old
        if screen:
            dep.progress_view.progress(msg,count,max)
        else:
            if msg:
                if bar_len_old:
                    bar_len_old = 0
                    print("\n")
                print(f"{msg}: ({count} of {max})")
            else:
                bar_len = int((count*40)/max)
                if bar_len_old != bar_len:
                    print(f"{'*' * (bar_len-bar_len_old)}",end='',flush=True)
                bar_len_old = bar_len
    def clear():
        if screen:
            dep.progress_view.clear()
    def clear_curses(screen):
        if screen:
            curses_off(screen)

    #
    # Trigger bitbake "generateDepTreeEvent"
    #

    cmdline = ''
    try:
        params.updateToServer(server, os.environ.copy())
        params.updateFromServer(server)
        cmdline = params.parseActions()
        if not cmdline:
            clear_curses(screen)
            print("ERROR: nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
            return 1,cmdline
        if 'msg' in cmdline and cmdline['msg']:
            clear_curses(screen)
            print('ERROR: ' + cmdline['msg'])
            return 1,cmdline
        cmdline = cmdline['action']
        if not cmdline or cmdline[0] != "generateDotGraph":
            clear_curses(screen)
            print("ERROR: This UI requires the -g option")
            return 1,cmdline
        ret, error = server.runCommand(["generateDepTreeEvent", cmdline[1], cmdline[2]])
        if error:
            clear_curses(screen)
            print("ERROR: running command '%s': %s" % (cmdline, error))
            return 1,cmdline
        elif not ret:
            clear_curses(screen)
            print("ERROR: running command '%s': returned %s" % (cmdline, ret))
            return 1,cmdline
    except client.Fault as x:
        clear_curses(screen)
        print("ERROR: XMLRPC Fault getting commandline:\n %s" % x)
        return 1,cmdline
    except Exception as e:
        clear_curses(screen)
        print("ERROR: in startup:\n %s" % traceback.format_exc())
        return 1,cmdline

    #
    # Receive data from bitbake
    #

    progress_total = 0
    load_bitbake = True
    quit = False
    try:
        while load_bitbake:
            try:
                event = eventHandler.waitEvent(0.25)
                if quit:
                    _, error = server.runCommand(["stateForceShutdown"])
                    clear_curses(screen)
                    if error:
                        print('Unable to cleanly stop: %s' % error)
                    break

                if event is None:
                    continue

                if isinstance(event, bb.event.CacheLoadStarted):
                    progress_total = event.total
                    progress('Loading Cache',0,progress_total)
                    continue

                if isinstance(event, bb.event.CacheLoadProgress):
                    x = event.current
                    progress('',x,progress_total)
                    continue

                if isinstance(event, bb.event.CacheLoadCompleted):
                    clear()
                    progress('Bitbake... ',1,2)
                    continue

                if isinstance(event, bb.event.ParseStarted):
                    progress_total = event.total
                    progress('Processing recipes',0,progress_total)
                    if progress_total == 0:
                        continue

                if isinstance(event, bb.event.ParseProgress):
                    x = event.current
                    progress('',x,progress_total)
                    continue

                if isinstance(event, bb.event.ParseCompleted):
                    progress('Generating dependency tree',0,3)
                    continue

                if isinstance(event, bb.event.DepTreeGenerated):
                    progress('Generating dependency tree',1,3)
                    dep.parse(event._depgraph)
                    progress('Generating dependency tree',2,3)

                if isinstance(event, bb.command.CommandCompleted):
                    load_bitbake = False
                    progress('Generating dependency tree',3,3)
                    clear()
                    if screen:
                        dep.help_bar_view.show_help(True)
                    continue

                if isinstance(event, bb.event.NoProvider):
                    clear_curses(screen)
                    print('ERROR: %s' % event)

                    _, error = server.runCommand(["stateShutdown"])
                    if error:
                        print('ERROR: Unable to cleanly shutdown: %s' % error)
                    return 1,cmdline

                if isinstance(event, bb.command.CommandFailed):
                    clear_curses(screen)
                    print('ERROR: ' + str(event))
                    return event.exitcode,cmdline

                if isinstance(event, bb.command.CommandExit):
                    clear_curses(screen)
                    return event.exitcode,cmdline

                if isinstance(event, bb.cooker.CookerExit):
                    break

                continue
            except EnvironmentError as ioerror:
                # ignore interrupted io
                if ioerror.args[0] == 4:
                    pass
            except KeyboardInterrupt:
                if shutdown == 2:
                    clear_curses(screen)
                    print("\nThird Keyboard Interrupt, exit.\n")
                    break
                if shutdown == 1:
                    clear_curses(screen)
                    print("\nSecond Keyboard Interrupt, stopping...\n")
                    _, error = server.runCommand(["stateForceShutdown"])
                    if error:
                        print('Unable to cleanly stop: %s' % error)
                if shutdown == 0:
                    clear_curses(screen)
                    print("\nKeyboard Interrupt, closing down...\n")
                    _, error = server.runCommand(["stateShutdown"])
                    if error:
                        print('Unable to cleanly shutdown: %s' % error)
                shutdown = shutdown + 1
                pass
    except Exception as e:
        # Safe exit on error
        clear_curses(screen)
        print("Exception : %s" % e)
        print("Exception in startup:\n %s" % traceback.format_exc())

    return 0,cmdline

#################################################
### main
###

SCREEN_COL_MIN = 83
SCREEN_ROW_MIN = 26

def main(server, eventHandler, params):
    global verbose
    global sort_model
    global print_model
    global is_printed
    global is_filter
    global screen_too_small

    shutdown = 0
    screen_too_small = False
    quit = False

    # Unit test with no terminal?
    if unit_test_noterm:
        # Load bitbake, test that there is valid dependency data, then exit
        screen = None
        print("* UNIT TEST:START")
        dep = DepExplorer(screen)
        print("* UNIT TEST:BITBAKE FETCH")
        ret,cmdline = bitbake_load(server, eventHandler, params, dep, None, screen)
        if ret:
            print("* UNIT TEST: BITBAKE FAILED")
            return ret
        # Test the acquired dependency data
        quilt_native_deps = 0
        quilt_native_rdeps = 0
        quilt_deps = 0
        quilt_rdeps = 0
        for i,task_obj in enumerate(dep.depends_model):
            if TYPE_DEP == task_obj[0]:
                task = task_obj[1]
                if task.startswith('quilt-native'):
                    quilt_native_deps += 1
                elif task.startswith('quilt'):
                    quilt_deps += 1
            elif TYPE_RDEP == task_obj[0]:
                task = task_obj[1]
                if task.startswith('quilt-native'):
                    quilt_native_rdeps += 1
                elif task.startswith('quilt'):
                    quilt_rdeps += 1
        # Print results
        failed = False
        if 0 < len(dep.depends_model):
             print(f"Pass:Bitbake dependency count = {len(dep.depends_model)}")
        else:
            failed = True
            print(f"FAIL:Bitbake dependency count = 0")
        if quilt_native_deps:
             print(f"Pass:Quilt-native depends count = {quilt_native_deps}")
        else:
            failed = True
            print(f"FAIL:Quilt-native depends count = 0")
        if quilt_native_rdeps:
             print(f"Pass:Quilt-native rdepends count = {quilt_native_rdeps}")
        else:
            failed = True
            print(f"FAIL:Quilt-native rdepends count = 0")
        if quilt_deps:
             print(f"Pass:Quilt depends count = {quilt_deps}")
        else:
            failed = True
            print(f"FAIL:Quilt depends count = 0")
        if quilt_rdeps:
             print(f"Pass:Quilt rdepends count = {quilt_rdeps}")
        else:
            failed = True
            print(f"FAIL:Quilt rdepends count = 0")
        print("* UNIT TEST:STOP")
        return failed

    # Help method to dynamically test parent window too small
    def check_screen_size(dep, active_package):
        global screen_too_small
        rows, cols = screen.getmaxyx()
        if (rows >= SCREEN_ROW_MIN) and (cols >= SCREEN_COL_MIN):
            if screen_too_small:
                # Now big enough, remove error message and redraw screen
                dep.draw_frames()
                active_package.cursor_on(True)
                screen_too_small = False
            return True
        # Test on App init
        if not dep:
            # Do not start this app if screen not big enough
            curses.endwin()
            print("")
            print("ERROR(Taskexp_cli): Mininal screen size is %dx%d" % (SCREEN_COL_MIN,SCREEN_ROW_MIN))
            print("Current screen is Cols=%s,Rows=%d" % (cols,rows))
            return False
        # First time window too small
        if not screen_too_small:
            active_package.cursor_on(False)
            dep.screen.addstr(0,2,'[BIGGER WINDOW PLEASE]', curses.color_pair(CURSES_WARNING) | curses.A_BLINK)
            screen_too_small = True
        return False

    # Helper method to turn off curses mode
    def curses_off(screen):
        if not screen: return
        # Safe error exit
        screen.keypad(False)
        curses.echo()
        curses.curs_set(1)
        curses.endwin()

        if unit_test_results:
            print('\nUnit Test Results:')
            for line in unit_test_results:
                print(" %s" % line)

    #
    # Initialize the ncurse environment
    #

    screen = curses.initscr()
    try:
        if not check_screen_size(None, None):
            exit(1)
        try:
            curses.start_color()
            curses.use_default_colors();
            curses.init_pair(0xFF, curses.COLOR_BLACK, curses.COLOR_WHITE);
            curses.init_pair(CURSES_NORMAL, curses.COLOR_WHITE, curses.COLOR_BLACK)
            curses.init_pair(CURSES_HIGHLIGHT, curses.COLOR_WHITE, curses.COLOR_BLUE)
            curses.init_pair(CURSES_WARNING, curses.COLOR_WHITE, curses.COLOR_RED)
        except:
            curses.endwin()
            print("")
            print("ERROR(Taskexp_cli): Requires 256 colors. Please use this or the equivalent:")
            print("  $ export TERM='xterm-256color'")
            exit(1)

        screen.keypad(True)
        curses.noecho()
        curses.curs_set(0)
        screen.refresh();
    except Exception as e:
        # Safe error exit
        curses_off(screen)
        print("Exception : %s" % e)
        print("Exception in startup:\n %s" % traceback.format_exc())
        exit(1)

    try:
        #
        # Instantiate the presentation layers
        #

        dep = DepExplorer(screen)

        #
        # Prepare bitbake
        #

        # Fetch bitbake dependecy data
        ret,cmdline = bitbake_load(server, eventHandler, params, dep, curses_off, screen)
        if ret: return ret

        #
        # Preset the views
        #

        # Cmdline example = ['generateDotGraph', ['acl', 'zlib'], 'build']
        primary_packages = cmdline[1]
        dep.package_view.set_primary(primary_packages)
        dep.dep_view.set_primary(primary_packages)
        dep.reverse_view.set_primary(primary_packages)
        dep.help_box_view.set_primary(primary_packages)
        dep.help_bar_view.show_help(True)
        active_package = dep.package_view
        active_package.cursor_on(True)
        dep.select(primary_packages[0]+'.')
        if unit_test:
            alert('UNIT_TEST',screen)

        # Help method to start/stop the filter feature
        def filter_mode(new_filter_status):
            global is_filter
            if is_filter == new_filter_status:
                # Ignore no changes
                return
            if not new_filter_status:
                # Turn off
                curses.curs_set(0)
                #active_package.cursor_on(False)
                active_package = dep.package_view
                active_package.cursor_on(True)
                is_filter = False
                dep.help_bar_view.show_help(True)
                dep.filter_str = ''
                dep.select('')
            else:
                # Turn on
                curses.curs_set(1)
                dep.help_bar_view.show_help(False)
                dep.filter_view.clear()
                dep.filter_view.show(True)
                dep.filter_view.show_prompt()
                is_filter = True

        #
        # Main user loop
        #

        while not quit:
            if is_filter:
                dep.filter_view.show_prompt()
            if unit_test:
                c = unit_test_action(active_package)
            else:
                c = screen.getch()
            ch = chr(c)

            # Do not draw if window now too small
            if not check_screen_size(dep,active_package):
                continue

            if verbose:
                if c == CHAR_RETURN:
                    screen.addstr(0, 4, "|%3d,CR |" % (c))
                else:
                    screen.addstr(0, 4, "|%3d,%3s|" % (c,chr(c)))

            # pre-map alternate filter close keys
            if is_filter and (c == CHAR_ESCAPE):
                # Alternate exit from filter
                ch = '/'
                c = ord(ch)

            # Filter and non-filter mode command keys
            # https://docs.python.org/3/library/curses.html
            if c in (curses.KEY_UP,CHAR_UP):
                active_package.line_up()
                if active_package == dep.package_view:
                    dep.select('',only_update_dependents=True)
            elif c in (curses.KEY_DOWN,CHAR_DOWN):
                active_package.line_down()
                if active_package == dep.package_view:
                    dep.select('',only_update_dependents=True)
            elif curses.KEY_PPAGE == c:
                active_package.page_up()
                if active_package == dep.package_view:
                    dep.select('',only_update_dependents=True)
            elif curses.KEY_NPAGE == c:
                active_package.page_down()
                if active_package == dep.package_view:
                    dep.select('',only_update_dependents=True)
            elif CHAR_TAB == c:
                # Tab between boxes
                active_package.cursor_on(False)
                if active_package == dep.package_view:
                    active_package = dep.dep_view
                elif active_package == dep.dep_view:
                    active_package = dep.reverse_view
                else:
                    active_package = dep.package_view
                active_package.cursor_on(True)
            elif curses.KEY_BTAB == c:
                # Shift-Tab reverse between boxes
                active_package.cursor_on(False)
                if active_package == dep.package_view:
                    active_package = dep.reverse_view
                elif active_package == dep.reverse_view:
                    active_package = dep.dep_view
                else:
                    active_package = dep.package_view
                active_package.cursor_on(True)
            elif (CHAR_RETURN == c):
                # CR to select
                selected = active_package.get_selected()
                if selected:
                    active_package.cursor_on(False)
                    active_package = dep.package_view
                    filter_mode(False)
                    dep.select(selected)
                else:
                    filter_mode(False)
                    dep.select(primary_packages[0]+'.')

            elif '/' == ch: # Enter/exit dep.filter_view
                if is_filter:
                    filter_mode(False)
                else:
                    filter_mode(True)
            elif is_filter:
                # If in filter mode, re-direct all these other keys to the filter box
                result = dep.filter_view.input(c,ch)
                dep.filter_str = dep.filter_view.filter_str
                dep.select('')

            # Non-filter mode command keys
            elif 'p' == ch:
                dep.print_deps(whole_group=False)
            elif 'P' == ch:
                dep.print_deps(whole_group=True)
            elif 'w' == ch:
                # Toggle the print model
                if print_model == PRINT_MODEL_1:
                    print_model = PRINT_MODEL_2
                else:
                    print_model = PRINT_MODEL_1
            elif 's' == ch:
                # Toggle the sort model
                if sort_model == SORT_DEPS:
                    sort_model = SORT_ALPHA
                elif sort_model == SORT_ALPHA:
                    if SORT_BITBAKE_ENABLE:
                        sort_model = TASK_SORT_BITBAKE
                    else:
                        sort_model = SORT_DEPS
                else:
                    sort_model = SORT_DEPS
                active_package.cursor_on(False)
                current_task = active_package.get_selected()
                dep.package_view.sort()
                dep.dep_view.sort()
                dep.reverse_view.sort()
                active_package = dep.package_view
                active_package.cursor_on(True)
                dep.select(current_task)
                # Announce the new sort model
                alert("SORT=%s" % ("ALPHA" if (sort_model == SORT_ALPHA) else "DEPS"),screen)
                alert('',screen)

            elif 'q' == ch:
                quit = True
            elif ch in ('h','?'):
                dep.help_box_view.show_help(True)
                dep.select(active_package.get_selected())

            #
            # Debugging commands
            #

            elif 'V' == ch:
                verbose = not verbose
                alert('Verbose=%s' % str(verbose),screen)
                alert('',screen)
            elif 'R' == ch:
                screen.refresh()
            elif 'B' == ch:
                # Progress bar unit test
                dep.progress_view.progress('Test',0,40)
                curses.napms(1000)
                dep.progress_view.progress('',10,40)
                curses.napms(1000)
                dep.progress_view.progress('',20,40)
                curses.napms(1000)
                dep.progress_view.progress('',30,40)
                curses.napms(1000)
                dep.progress_view.progress('',40,40)
                curses.napms(1000)
                dep.progress_view.clear()
                dep.help_bar_view.show_help(True)
            elif 'Q' == ch:
                # Simulated error
                curses_off(screen)
                print('ERROR: simulated error exit')
                return 1

        # Safe exit
        curses_off(screen)
    except Exception as e:
        # Safe exit on error
        curses_off(screen)
        print("Exception : %s" % e)
        print("Exception in startup:\n %s" % traceback.format_exc())

    # Reminder to pick up your printed results
    if is_printed:
        print("")
        print("You have output ready!")
        print("  * Your printed dependency file is: %s" % print_file_name)
        print("  * Your previous results  saved in: %s" % print_file_backup_name)
        print("")
