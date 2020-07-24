# pyshyacc.py - PLY grammar definition for pysh
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

"""PLY grammar file.
"""
import os.path
import sys

import bb.pysh.pyshlex as pyshlex
tokens = pyshlex.tokens

from ply import yacc
import bb.pysh.sherrors as sherrors
    
class IORedirect:
    def __init__(self, op, filename, io_number=None):
        self.op = op
        self.filename = filename
        self.io_number = io_number
        
class HereDocument:
    def __init__(self, op, name, content, io_number=None):
        self.op = op
        self.name = name
        self.content = content
        self.io_number = io_number

def make_io_redirect(p):
    """Make an IORedirect instance from the input 'io_redirect' production."""
    name, io_number, io_target = p
    assert name=='io_redirect'
    
    if io_target[0]=='io_file':
        io_type, io_op, io_file = io_target
        return IORedirect(io_op, io_file, io_number)
    elif io_target[0]=='io_here':
        io_type, io_op, io_name, io_content = io_target
        return HereDocument(io_op, io_name, io_content, io_number)
    else:
        assert False, "Invalid IO redirection token %s" % repr(io_type)
        
class SimpleCommand:
    """
    assigns contains (name, value) pairs.
    """
    def __init__(self, words, redirs, assigns):
        self.words = list(words)
        self.redirs = list(redirs)
        self.assigns = list(assigns)

class Pipeline:
    def __init__(self, commands, reverse_status=False):
        self.commands = list(commands)
        assert self.commands    #Grammar forbids this
        self.reverse_status = reverse_status
        
class AndOr:
    def __init__(self, op, left, right):
        self.op = str(op)
        self.left = left
        self.right = right
        
class ForLoop:
    def __init__(self, name, items, cmds):
        self.name = str(name)
        self.items = list(items)
        self.cmds = list(cmds)
        
class WhileLoop:
    def __init__(self, condition, cmds):
        self.condition = list(condition)
        self.cmds = list(cmds)
        
class UntilLoop:
    def __init__(self, condition, cmds):
        self.condition = list(condition)
        self.cmds = list(cmds)

class FunDef:
    def __init__(self, name, body):
        self.name = str(name)
        self.body = body
        
class BraceGroup:
    def __init__(self, cmds):
        self.cmds = list(cmds)
        
class IfCond:
    def __init__(self, cond, if_cmds, else_cmds):
        self.cond = list(cond)
        self.if_cmds = if_cmds
        self.else_cmds = else_cmds

class Case:
    def __init__(self, name, items):
        self.name = name
        self.items = items
        
class SubShell:
    def __init__(self, cmds):
        self.cmds = cmds

class RedirectList:
    def __init__(self, cmd, redirs):
        self.cmd = cmd
        self.redirs = list(redirs)
        
def get_production(productions, ptype):
    """productions must be a list of production tuples like (name, obj) where
    name is the production string identifier.
    Return the first production named 'ptype'. Raise KeyError if None can be
    found.
    """
    for production in productions:
        if production is not None and production[0]==ptype:
            return production
    raise KeyError(ptype)
    
#-------------------------------------------------------------------------------
# PLY grammar definition
#-------------------------------------------------------------------------------

def p_multiple_commands(p):
    """multiple_commands : newline_sequence
                         | complete_command
                         | multiple_commands complete_command"""
    if len(p)==2:
        if p[1] is not None:
            p[0] = [p[1]]
        else:
            p[0] = []
    else:
        p[0] = p[1] + [p[2]]

def p_complete_command(p):
    """complete_command : list separator
                        | list"""
    if len(p)==3 and p[2] and p[2][1] == '&':
        p[0] = ('async', p[1])
    else:
        p[0] = p[1]
                 
def p_list(p):
    """list : list separator_op and_or
            |                   and_or"""
    if len(p)==2:
        p[0] = [p[1]]
    else:
        #if p[2]!=';':
        #    raise NotImplementedError('AND-OR list asynchronous execution is not implemented')
        p[0] = p[1] + [p[3]]
       
def p_and_or(p):
    """and_or : pipeline
              | and_or AND_IF linebreak pipeline
              | and_or OR_IF  linebreak pipeline"""
    if len(p)==2:
        p[0] = p[1]
    else:
        p[0] = ('and_or', AndOr(p[2], p[1], p[4]))
        
def p_maybe_bang_word(p):
    """maybe_bang_word : Bang"""
    p[0] = ('maybe_bang_word', p[1])
            
def p_pipeline(p):
    """pipeline : pipe_sequence
                | bang_word pipe_sequence"""
    if len(p)==3:
        p[0] = ('pipeline', Pipeline(p[2][1:], True))
    else:
        p[0] = ('pipeline', Pipeline(p[1][1:]))

def p_pipe_sequence(p):
    """pipe_sequence : command
                     | pipe_sequence PIPE linebreak command"""
    if len(p)==2:
        p[0] = ['pipe_sequence', p[1]]
    else:
        p[0] = p[1] + [p[4]]

def p_command(p):
    """command : simple_command
               | compound_command
               | compound_command redirect_list
               | function_definition"""
        
    if p[1][0] in ( 'simple_command', 
                    'for_clause',
                    'while_clause',
                    'until_clause',
                    'case_clause',
                    'if_clause',
                    'function_definition',
                    'subshell',
                    'brace_group',):
        if len(p) == 2:
            p[0] = p[1]
        else:
            p[0] = ('redirect_list', RedirectList(p[1], p[2][1:]))
    else:
        raise NotImplementedError('%s command is not implemented' % repr(p[1][0]))

def p_compound_command(p):
    """compound_command : brace_group
                        | subshell
                        | for_clause
                        | case_clause
                        | if_clause
                        | while_clause
                        | until_clause"""
    p[0] = p[1]

def p_subshell(p):
    """subshell : LPARENS compound_list RPARENS"""
    p[0] = ('subshell', SubShell(p[2][1:]))

def p_compound_list(p):
    """compound_list : term
                     | newline_list term
                     |              term separator
                     | newline_list term separator"""
    productions = p[1:]           
    try:
        sep = get_production(productions, 'separator')
        if sep[1]!=';':
            raise NotImplementedError()
    except KeyError:
        pass
    term = get_production(productions, 'term')
    p[0] = ['compound_list'] + term[1:]

def p_term(p):
    """term : term separator and_or
            |                and_or"""
    if len(p)==2:
        p[0] = ['term', p[1]]
    else:
        if p[2] is not None and p[2][1] == '&':
            p[0] = ['term', ('async', p[1][1:])] + [p[3]]
        else:
            p[0] = p[1] + [p[3]]
            
def p_maybe_for_word(p):
    # Rearrange 'For' priority wrt TOKEN. See p_for_word
    """maybe_for_word : For"""
    p[0] = ('maybe_for_word', p[1])

def p_for_clause(p):
    """for_clause : for_word name linebreak                            do_group
                  | for_word name linebreak in          sequential_sep do_group
                  | for_word name linebreak in wordlist sequential_sep do_group"""
    productions = p[1:]
    do_group = get_production(productions, 'do_group')
    try:
        items = get_production(productions, 'in')[1:]
    except KeyError:
        raise NotImplementedError('"in" omission is not implemented')
        
    try:
        items = get_production(productions, 'wordlist')[1:]
    except KeyError:
        items = []
        
    name = p[2]
    p[0] = ('for_clause', ForLoop(name, items, do_group[1:]))

def p_name(p):
    """name : token""" #Was NAME instead of token
    p[0] = p[1]

def p_in(p):
    """in : In"""
    p[0] = ('in', p[1])

def p_wordlist(p):
    """wordlist : wordlist token
                |          token"""
    if len(p)==2:
        p[0] = ['wordlist', ('TOKEN', p[1])]
    else:
        p[0] = p[1] + [('TOKEN', p[2])]

def p_case_clause(p):
    """case_clause : Case token linebreak in linebreak case_list    Esac
                   | Case token linebreak in linebreak case_list_ns Esac
                   | Case token linebreak in linebreak              Esac"""
    if len(p) < 8:
        items = []
    else:
        items = p[6][1:]
    name = p[2]
    p[0] = ('case_clause', Case(name, [c[1] for c in items]))
       
def p_case_list_ns(p):
    """case_list_ns : case_list case_item_ns
                    |           case_item_ns"""
    p_case_list(p)
      
def p_case_list(p):
    """case_list : case_list case_item
                 |           case_item"""
    if len(p)==2:
        p[0] = ['case_list', p[1]]
    else:
        p[0] = p[1] + [p[2]]
        
def p_case_item_ns(p):
    """case_item_ns :         pattern RPARENS               linebreak
                    |         pattern RPARENS compound_list linebreak
                    | LPARENS pattern RPARENS               linebreak
                    | LPARENS pattern RPARENS compound_list linebreak"""
    p_case_item(p)
                 
def p_case_item(p):
    """case_item :         pattern RPARENS linebreak     DSEMI linebreak
                 |         pattern RPARENS compound_list DSEMI linebreak
                 | LPARENS pattern RPARENS linebreak     DSEMI linebreak
                 | LPARENS pattern RPARENS compound_list DSEMI linebreak"""
    if len(p) < 7:
        name = p[1][1:]
    else:
        name = p[2][1:]

    try:
        cmds = get_production(p[1:], "compound_list")[1:]
    except KeyError:
        cmds = []

    p[0] = ('case_item', (name, cmds))
                 
def p_pattern(p):
    """pattern :              token
               | pattern PIPE token"""
    if len(p)==2:
        p[0] = ['pattern', ('TOKEN', p[1])]
    else:
        p[0] = p[1] + [('TOKEN', p[2])]

def p_maybe_if_word(p):
    # Rearrange 'If' priority wrt TOKEN. See p_if_word
    """maybe_if_word : If"""
    p[0] = ('maybe_if_word', p[1])

def p_maybe_then_word(p):
    # Rearrange 'Then' priority wrt TOKEN. See p_then_word
    """maybe_then_word : Then"""
    p[0] = ('maybe_then_word', p[1])
                 
def p_if_clause(p):
    """if_clause : if_word compound_list then_word compound_list else_part Fi
                 | if_word compound_list then_word compound_list           Fi"""
    else_part = []
    if len(p)==7:
        else_part = p[5]
    p[0] = ('if_clause', IfCond(p[2][1:], p[4][1:], else_part))
                 
def p_else_part(p):
    """else_part : Elif compound_list then_word compound_list else_part
                 | Elif compound_list then_word compound_list
                 | Else compound_list"""
    if len(p)==3:
        p[0] = p[2][1:]
    else:
        else_part = []
        if len(p)==6:
            else_part = p[5]
        p[0] = ('elif', IfCond(p[2][1:], p[4][1:], else_part))
                 
def p_while_clause(p):
    """while_clause : While compound_list do_group"""
    p[0] = ('while_clause', WhileLoop(p[2][1:], p[3][1:]))
    
def p_maybe_until_word(p):
    # Rearrange 'Until' priority wrt TOKEN. See p_until_word
    """maybe_until_word : Until"""
    p[0] = ('maybe_until_word', p[1])
           
def p_until_clause(p):
    """until_clause : until_word compound_list do_group"""
    p[0] = ('until_clause', UntilLoop(p[2][1:], p[3][1:]))
                 
def p_function_definition(p):
    """function_definition : fname LPARENS RPARENS linebreak function_body"""
    p[0] = ('function_definition', FunDef(p[1], p[5]))
                 
def p_function_body(p):
    """function_body : compound_command
                     | compound_command redirect_list"""
    if len(p)!=2:
        raise NotImplementedError('functions redirections lists are not implemented')    
    p[0] = p[1]    

def p_fname(p):
    """fname : TOKEN""" #Was NAME instead of token
    p[0] = p[1]

def p_brace_group(p):
    """brace_group : Lbrace compound_list Rbrace"""
    p[0] = ('brace_group', BraceGroup(p[2][1:]))

def p_maybe_done_word(p):
    #See p_assignment_word for details.
    """maybe_done_word : Done"""
    p[0] = ('maybe_done_word', p[1])

def p_maybe_do_word(p):
    """maybe_do_word : Do"""
    p[0] = ('maybe_do_word', p[1])

def p_do_group(p):
    """do_group : do_word compound_list done_word"""
    #Do group contains a list of AndOr
    p[0] = ['do_group'] + p[2][1:]

def p_simple_command(p):
    """simple_command : cmd_prefix cmd_word cmd_suffix
                      | cmd_prefix cmd_word
                      | cmd_prefix
                      | cmd_name cmd_suffix
                      | cmd_name"""
    words, redirs, assigns = [], [], []
    for e in p[1:]:
        name = e[0]
        if name in ('cmd_prefix', 'cmd_suffix'):
            for sube in e[1:]:
                subname = sube[0]
                if subname=='io_redirect':
                    redirs.append(make_io_redirect(sube))
                elif subname=='ASSIGNMENT_WORD':
                    assigns.append(sube)
                else:
                    words.append(sube)
        elif name in ('cmd_word', 'cmd_name'):
            words.append(e)
            
    cmd = SimpleCommand(words, redirs, assigns)
    p[0] = ('simple_command', cmd)

def p_cmd_name(p):
    """cmd_name : TOKEN"""
    p[0] = ('cmd_name', p[1])
    
def p_cmd_word(p):
    """cmd_word : token"""
    p[0] = ('cmd_word', p[1])

def p_maybe_assignment_word(p):
    #See p_assignment_word for details.
    """maybe_assignment_word : ASSIGNMENT_WORD"""
    p[0] = ('maybe_assignment_word', p[1])
    
def p_cmd_prefix(p):
    """cmd_prefix :            io_redirect
                  | cmd_prefix io_redirect
                  |            assignment_word
                  | cmd_prefix assignment_word"""
    try:
        prefix = get_production(p[1:], 'cmd_prefix')
    except KeyError:
        prefix = ['cmd_prefix']
        
    try:
        value = get_production(p[1:], 'assignment_word')[1]
        value = ('ASSIGNMENT_WORD', value.split('=', 1))
    except KeyError:        
        value = get_production(p[1:], 'io_redirect')
    p[0] = prefix + [value]
                                  
def p_cmd_suffix(p):
    """cmd_suffix   :            io_redirect
                    | cmd_suffix io_redirect
                    |            token
                    | cmd_suffix token
                    |            maybe_for_word
                    | cmd_suffix maybe_for_word
                    |            maybe_done_word
                    | cmd_suffix maybe_done_word
                    |            maybe_do_word
                    | cmd_suffix maybe_do_word
                    |            maybe_until_word
                    | cmd_suffix maybe_until_word
                    |            maybe_assignment_word
                    | cmd_suffix maybe_assignment_word
                    |            maybe_if_word
                    | cmd_suffix maybe_if_word
                    |            maybe_then_word
                    | cmd_suffix maybe_then_word
                    |            maybe_bang_word
                    | cmd_suffix maybe_bang_word"""
    try:
        suffix = get_production(p[1:], 'cmd_suffix')
        token = p[2]
    except KeyError:
        suffix = ['cmd_suffix']
        token = p[1]
        
    if isinstance(token, tuple):
        if token[0]=='io_redirect':
            p[0] = suffix + [token]
        else:
            #Convert maybe_*  to TOKEN if necessary
            p[0] = suffix + [('TOKEN', token[1])]
    else:
        p[0] = suffix + [('TOKEN', token)]
                 
def p_redirect_list(p):
    """redirect_list : io_redirect
                     | redirect_list io_redirect"""
    if len(p) == 2:
        p[0] = ['redirect_list', make_io_redirect(p[1])]
    else:
        p[0] = p[1] + [make_io_redirect(p[2])]
    
def p_io_redirect(p):
    """io_redirect :           io_file
                   | IO_NUMBER io_file
                   |           io_here
                   | IO_NUMBER io_here"""
    if len(p)==3:
        p[0] = ('io_redirect', p[1], p[2])
    else:
        p[0] = ('io_redirect', None, p[1])
    
def p_io_file(p):
    #Return the tuple (operator, filename)
    """io_file : LESS      filename
               | LESSAND   filename
               | GREATER   filename
               | GREATAND  filename
               | DGREAT    filename
               | LESSGREAT filename
               | CLOBBER   filename"""
    #Extract the filename from the file
    p[0] = ('io_file', p[1], p[2][1])

def p_filename(p):
    #Return the filename
    """filename : TOKEN"""
    p[0] = ('filename', p[1])
        
def p_io_here(p):
    """io_here : DLESS here_end
               | DLESSDASH here_end"""
    p[0] = ('io_here', p[1], p[2][1], p[2][2])

def p_here_end(p):
    """here_end : HERENAME TOKEN"""
    p[0] = ('here_document', p[1], p[2])
    
def p_newline_sequence(p):
    # Nothing in the grammar can handle leading NEWLINE productions, so add
    # this one with the lowest possible priority relatively to newline_list.
    """newline_sequence : newline_list"""
    p[0] = None
    
def p_newline_list(p):
    """newline_list : NEWLINE
                    | newline_list NEWLINE"""
    p[0] = None
                    
def p_linebreak(p):
    """linebreak : newline_list
                 | empty"""
    p[0] = None

def p_separator_op(p):                 
    """separator_op : COMMA
                    | COMMA COMMA
                    | AMP"""
    p[0] = p[1]

def p_separator(p):
    """separator : separator_op linebreak
                 | newline_list"""
    if len(p)==2:
        #Ignore newlines
        p[0] = None
    else:
        #Keep the separator operator
        p[0] = ('separator', p[1])
                 
def p_sequential_sep(p):
    """sequential_sep : COMMA linebreak
                      | newline_list"""
    p[0] = None

# Low priority TOKEN => for_word conversion.
# Let maybe_for_word be used as a token when necessary in higher priority
# rules. 
def p_for_word(p):
    """for_word : maybe_for_word"""
    p[0] = p[1]

def p_if_word(p):
    """if_word : maybe_if_word"""
    p[0] = p[1]

def p_then_word(p):
    """then_word : maybe_then_word"""
    p[0] = p[1]

def p_done_word(p):
    """done_word : maybe_done_word"""
    p[0] = p[1]

def p_do_word(p):
    """do_word : maybe_do_word"""
    p[0] = p[1]
    
def p_until_word(p):
    """until_word : maybe_until_word"""
    p[0] = p[1]
    
def p_assignment_word(p):
    """assignment_word : maybe_assignment_word"""
    p[0] = ('assignment_word', p[1][1])
    
def p_bang_word(p):
    """bang_word : maybe_bang_word"""
    p[0] = ('bang_word', p[1][1])

def p_token(p):
    """token : TOKEN
             | Fi"""
    p[0] = p[1]

def p_empty(p):
    'empty :'
    p[0] = None
    
# Error rule for syntax errors
def p_error(p):
    msg = []
    w = msg.append
    if p:
        w('%r\n' % p)
        w('followed by:\n')
        for i in range(5):
            n = yacc.token()
            if not n:
                break
            w('  %r\n' % n)
    else:
        w('Unexpected EOF')
    raise sherrors.ShellSyntaxError(''.join(msg))

# Build the parser
try:
    import pyshtables
except ImportError:
    outputdir = os.path.dirname(__file__)
    if not os.access(outputdir, os.W_OK):
        outputdir = ''
    yacc.yacc(tabmodule = 'pyshtables', outputdir = outputdir, debug = 0)
else:
    yacc.yacc(tabmodule = 'pysh.pyshtables', write_tables = 0, debug = 0)


def parse(input, eof=False, debug=False):
    """Parse a whole script at once and return the generated AST and unconsumed
    data in a tuple.
    
    NOTE: eof is probably meaningless for now, the parser being unable to work
    in pull mode. It should be set to True.
    """
    lexer = pyshlex.PLYLexer()
    remaining = lexer.add(input, eof)
    if lexer.is_empty():
        return [], remaining
    if debug:
        debug = 2
    return yacc.parse(lexer=lexer, debug=debug), remaining

#-------------------------------------------------------------------------------
# AST rendering helpers
#-------------------------------------------------------------------------------    

def format_commands(v):
    """Return a tree made of strings and lists. Make command trees easier to
    display.
    """
    if isinstance(v, list):
        return [format_commands(c) for c in v]
    if isinstance(v, tuple):
        if len(v)==2 and isinstance(v[0], str) and not isinstance(v[1], str):
            if v[0] == 'async':
                return ['AsyncList', map(format_commands, v[1])]
            else:
                #Avoid decomposing tuples like ('pipeline', Pipeline(...))
                return format_commands(v[1])
        return format_commands(list(v))
    elif isinstance(v, IfCond):
        name = ['IfCond']
        name += ['if', map(format_commands, v.cond)]
        name += ['then', map(format_commands, v.if_cmds)]
        name += ['else', map(format_commands, v.else_cmds)]
        return name
    elif isinstance(v, ForLoop):
        name = ['ForLoop']
        name += [repr(v.name)+' in ', map(str, v.items)]
        name += ['commands', map(format_commands, v.cmds)]
        return name
    elif isinstance(v, AndOr):
        return [v.op, format_commands(v.left), format_commands(v.right)]
    elif isinstance(v, Pipeline):
        name = 'Pipeline'
        if v.reverse_status:
            name = '!' + name
        return [name, format_commands(v.commands)]
    elif isinstance(v, Case):
        name = ['Case']
        name += [v.name, format_commands(v.items)]
    elif isinstance(v, SimpleCommand):
        name = ['SimpleCommand']
        if v.words:                
            name += ['words', map(str, v.words)]
        if v.assigns:
            assigns = [tuple(a[1]) for a in v.assigns]
            name += ['assigns', map(str, assigns)]
        if v.redirs:
            name += ['redirs', map(format_commands, v.redirs)]
        return name
    elif isinstance(v, RedirectList):
        name = ['RedirectList']
        if v.redirs:
            name += ['redirs', map(format_commands, v.redirs)]
        name += ['command', format_commands(v.cmd)]
        return name
    elif isinstance(v, IORedirect):
        return ' '.join(map(str, (v.io_number, v.op, v.filename)))
    elif isinstance(v, HereDocument):
        return ' '.join(map(str, (v.io_number, v.op, repr(v.name), repr(v.content))))
    elif isinstance(v, SubShell):
        return ['SubShell', map(format_commands, v.cmds)]
    else:
        return repr(v)
             
def print_commands(cmds, output=sys.stdout):
    """Pretty print a command tree."""
    def print_tree(cmd, spaces, output):
        if isinstance(cmd, list):
            for c in cmd:
                print_tree(c, spaces + 3, output)              
        else:
            print >>output, ' '*spaces + str(cmd)
    
    formatted = format_commands(cmds)
    print_tree(formatted, 0, output)
    
    
def stringify_commands(cmds): 
    """Serialize a command tree as a string.
    
    Returned string is not pretty and is currently used for unit tests only.
    """   
    def stringify(value):
        output = []
        if isinstance(value, list):
            formatted = []
            for v in value:
                formatted.append(stringify(v))
            formatted = ' '.join(formatted)
            output.append(''.join(['<', formatted, '>']))
        else:
            output.append(value)
        return ' '.join(output)
            
    return stringify(format_commands(cmds))
    
        
def visit_commands(cmds, callable):
    """Visit the command tree and execute callable on every Pipeline and 
    SimpleCommand instances.
    """
    if isinstance(cmds, (tuple, list)):
        map(lambda c: visit_commands(c,callable), cmds)
    elif isinstance(cmds, (Pipeline, SimpleCommand)):
        callable(cmds)
