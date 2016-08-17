# pyshlex.py - PLY compatible lexer for pysh.
#
# Copyright 2007 Patrick Mezard
#
# This software may be used and distributed according to the terms
# of the GNU General Public License, incorporated herein by reference.

# TODO:
# - review all "char in 'abc'" snippets: the empty string can be matched
# - test line continuations within quoted/expansion strings
# - eof is buggy wrt sublexers
# - the lexer cannot really work in pull mode as it would be required to run
# PLY in pull mode. It was designed to work incrementally and it would not be
# that hard to enable pull mode.
import re
try:
    s = set()
    del s
except NameError:
    from Set import Set as set

from ply import lex
from sherrors import *

class NeedMore(Exception):
    pass

def is_blank(c):
    return c in (' ', '\t')
    
_RE_DIGITS = re.compile(r'^\d+$')

def are_digits(s):
    return _RE_DIGITS.search(s) is not None

_OPERATORS = dict([
    ('&&', 'AND_IF'),
    ('||', 'OR_IF'),
    (';;', 'DSEMI'),
    ('<<', 'DLESS'),
    ('>>', 'DGREAT'),
    ('<&', 'LESSAND'),
    ('>&', 'GREATAND'),
    ('<>', 'LESSGREAT'),
    ('<<-', 'DLESSDASH'),
    ('>|', 'CLOBBER'),
    ('&', 'AMP'),
    (';', 'COMMA'),
    ('<', 'LESS'),
    ('>', 'GREATER'),
    ('(', 'LPARENS'),
    (')', 'RPARENS'),
])

#Make a function to silence pychecker "Local variable shadows global"
def make_partial_ops():
    partials = {}
    for k in _OPERATORS:
        for i in range(1, len(k)+1):
            partials[k[:i]] = None
    return partials  

_PARTIAL_OPERATORS = make_partial_ops()    
        
def is_partial_op(s):
    """Return True if s matches a non-empty subpart of an operator starting
    at its first character.
    """
    return s in _PARTIAL_OPERATORS
    
def is_op(s):
    """If s matches an operator, returns the operator identifier. Return None
    otherwise.
    """
    return _OPERATORS.get(s)

_RESERVEDS = dict([
    ('if', 'If'),
    ('then', 'Then'),
    ('else', 'Else'),
    ('elif', 'Elif'),
    ('fi', 'Fi'),
    ('do', 'Do'),
    ('done', 'Done'),
    ('case', 'Case'),
    ('esac', 'Esac'),
    ('while', 'While'),
    ('until', 'Until'),
    ('for', 'For'),
    ('{', 'Lbrace'),
    ('}', 'Rbrace'),
    ('!', 'Bang'),
    ('in', 'In'),
    ('|', 'PIPE'),
])
    
def get_reserved(s):
    return _RESERVEDS.get(s)
    
_RE_NAME = re.compile(r'^[0-9a-zA-Z_]+$')

def is_name(s):
    return _RE_NAME.search(s) is not None

def find_chars(seq, chars):
    for i,v in enumerate(seq):
        if v in chars:
            return i,v
    return -1, None

class WordLexer:
    """WordLexer parse quoted or expansion expressions and return an expression
    tree. The input string can be any well formed sequence beginning with quoting
    or expansion character. Embedded expressions are handled recursively. The
    resulting tree is made of lists and strings. Lists represent quoted or
    expansion expressions. Each list first element is the opening separator,
    the last one the closing separator. In-between can be any number of strings
    or lists for sub-expressions. Non quoted/expansion expression can written as
    strings or as lists with empty strings as starting and ending delimiters.
    """

    NAME_CHARSET = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_'
    NAME_CHARSET = dict(zip(NAME_CHARSET, NAME_CHARSET))
    
    SPECIAL_CHARSET = '@*#?-$!0'
    
    #Characters which can be escaped depends on the current delimiters
    ESCAPABLE = {
        '`': set(['$', '\\', '`']),
        '"': set(['$', '\\', '`', '"']),
        "'": set(),
    }
        
    def __init__(self, heredoc = False):
        # _buffer is the unprocessed input characters buffer
        self._buffer = []
        # _stack is empty or contains a quoted list being processed
        # (this is the DFS path to the quoted expression being evaluated).
        self._stack = []
        self._escapable = None
        # True when parsing unquoted here documents
        self._heredoc = heredoc
        
    def add(self, data, eof=False):
        """Feed the lexer with more data. If the quoted expression can be
        delimited, return a tuple (expr, remaining) containing the expression
        tree and the unconsumed data.
        Otherwise, raise NeedMore.
        """
        self._buffer += list(data)
        self._parse(eof)
        
        result = self._stack[0]
        remaining = ''.join(self._buffer)
        self._stack = []
        self._buffer = []
        return result, remaining
        
    def _is_escapable(self, c, delim=None):
        if delim is None:
            if self._heredoc:
                # Backslashes works as if they were double quoted in unquoted
                # here-documents
                delim = '"'
            else:
                if len(self._stack)<=1:
                    return True
                delim = self._stack[-2][0]
            
        escapables = self.ESCAPABLE.get(delim, None)
        return escapables is None or c in escapables
        
    def _parse_squote(self, buf, result, eof):
        if not buf:
            raise NeedMore()
        try:
            pos = buf.index("'")
        except ValueError:
            raise NeedMore()
        result[-1] += ''.join(buf[:pos])
        result += ["'"]
        return pos+1, True
        
    def _parse_bquote(self, buf, result, eof):
        if not buf:
            raise NeedMore()
            
        if buf[0]=='\n':
            #Remove line continuations
            result[:] = ['', '', '']
        elif self._is_escapable(buf[0]):
            result[-1] += buf[0]
            result += ['']
        else:
            #Keep as such
            result[:] = ['', '\\'+buf[0], '']
        
        return 1, True
        
    def _parse_dquote(self, buf, result, eof):
        if not buf:
            raise NeedMore()
        pos, sep = find_chars(buf, '$\\`"')
        if pos==-1:
            raise NeedMore()
            
        result[-1] += ''.join(buf[:pos])
        if sep=='"':
            result += ['"']
            return pos+1, True
        else:
            #Keep everything until the separator and defer processing
            return pos, False
            
    def _parse_command(self, buf, result, eof):
        if not buf:
            raise NeedMore()

        chars = '$\\`"\''
        if result[0] == '$(':
            chars += ')'
        pos, sep = find_chars(buf, chars)
        if pos == -1:
            raise NeedMore()
            
        result[-1] += ''.join(buf[:pos])
        if (result[0]=='$(' and sep==')') or (result[0]=='`' and sep=='`'):
            result += [sep]
            return pos+1, True
        else:
            return pos, False
            
    def _parse_parameter(self, buf, result, eof):
        if not buf:
            raise NeedMore()
            
        pos, sep = find_chars(buf, '$\\`"\'}')
        if pos==-1:
            raise NeedMore()
            
        result[-1] += ''.join(buf[:pos])
        if sep=='}':
            result += [sep]
            return pos+1, True
        else:
            return pos, False
            
    def _parse_dollar(self, buf, result, eof):
        sep = result[0]
        if sep=='$':            
            if not buf:
                #TODO: handle empty $
                raise NeedMore()
            if buf[0]=='(':
                if len(buf)==1:
                    raise NeedMore()
                    
                if buf[1]=='(':
                    result[0] = '$(('
                    buf[:2] = []
                else:
                    result[0] = '$('
                    buf[:1] = []
                
            elif buf[0]=='{':
                result[0] = '${'
                buf[:1] = []
            else:
                if buf[0] in self.SPECIAL_CHARSET:
                    result[-1] = buf[0]
                    read = 1
                else:
                    for read,c in enumerate(buf):
                        if c not in self.NAME_CHARSET:
                            break
                    else:
                        if not eof:
                            raise NeedMore()
                        read += 1
                        
                    result[-1] += ''.join(buf[0:read])
                    
                if not result[-1]:
                    result[:] = ['', result[0], '']
                else:
                    result += [''] 
                return read,True
        
        sep = result[0]    
        if sep=='$(':
            parsefunc = self._parse_command
        elif sep=='${':
            parsefunc = self._parse_parameter
        else:
            raise NotImplementedError(sep)
            
        pos, closed = parsefunc(buf, result, eof)
        return pos, closed

    def _parse(self, eof):
        buf = self._buffer
        stack = self._stack
        recurse = False
    
        while 1:
            if not stack or recurse:
                if not buf:
                    raise NeedMore()
                if buf[0] not in ('"\\`$\''):
                    raise ShellSyntaxError('Invalid quoted string sequence')
                stack.append([buf[0], ''])
                buf[:1] = []
                recurse = False
                
            result = stack[-1]
            if result[0]=="'":
                parsefunc = self._parse_squote
            elif result[0]=='\\':
                parsefunc = self._parse_bquote
            elif result[0]=='"':
                parsefunc = self._parse_dquote
            elif result[0]=='`':
                parsefunc = self._parse_command
            elif result[0][0]=='$':
                parsefunc = self._parse_dollar
            else:
                raise NotImplementedError()
                
            read, closed = parsefunc(buf, result, eof)
                
            buf[:read] = []
            if closed:
                if len(stack)>1:
                    #Merge in parent expression
                    parsed = stack.pop()
                    stack[-1] += [parsed]
                    stack[-1] += ['']
                else:
                    break
            else:
                recurse = True

def normalize_wordtree(wtree):
    """Fold back every literal sequence (delimited with empty strings) into
    parent sequence.
    """
    def normalize(wtree):
        result = []
        for part in wtree[1:-1]:
            if isinstance(part, list):
                part = normalize(part)
                if part[0]=='':
                    #Move the part content back at current level
                    result += part[1:-1]
                    continue
            elif not part:
                #Remove empty strings
                continue
            result.append(part)
        if not result:
            result = ['']    
        return [wtree[0]] + result + [wtree[-1]]
                
    return normalize(wtree)
    
                
def make_wordtree(token, here_document=False):
    """Parse a delimited token and return a tree similar to the ones returned by
    WordLexer. token may contain any combinations of expansion/quoted fields and
    non-ones.
    """    
    tree = ['']
    remaining = token
    delimiters = '\\$`'
    if not here_document:
        delimiters += '\'"'
    
    while 1:
        pos, sep = find_chars(remaining, delimiters)
        if pos==-1:
            tree += [remaining, '']
            return normalize_wordtree(tree)
        tree.append(remaining[:pos])
        remaining = remaining[pos:]
        
        try:
            result, remaining = WordLexer(heredoc = here_document).add(remaining, True)
        except NeedMore:
            raise ShellSyntaxError('Invalid token "%s"')
        tree.append(result)
        
                
def wordtree_as_string(wtree):
    """Rewrite an expression tree generated by make_wordtree as string."""
    def visit(node, output):
        for child in node:
            if isinstance(child, list):
                visit(child, output)
            else:
                output.append(child)
    
    output = []
    visit(wtree, output)
    return ''.join(output)
    
    
def unquote_wordtree(wtree):
    """Fold the word tree while removing quotes everywhere. Other expansion
    sequences are joined as such.
    """
    def unquote(wtree):
        unquoted = []
        if wtree[0] in ('', "'", '"', '\\'):
            wtree = wtree[1:-1]
            
        for part in wtree:
            if isinstance(part, list):
                part = unquote(part)
            unquoted.append(part)
        return ''.join(unquoted)
            
    return unquote(wtree)
    
    
class HereDocLexer:
    """HereDocLexer delimits whatever comes from the here-document starting newline
    not included to the closing delimiter line included.
    """
    def __init__(self, op, delim):
        assert op in ('<<', '<<-')
        if not delim:
            raise ShellSyntaxError('invalid here document delimiter %s' % str(delim))
            
        self._op = op
        self._delim = delim
        self._buffer = []
        self._token = []
        
    def add(self, data, eof):
        """If the here-document was delimited, return a tuple (content, remaining).
        Raise NeedMore() otherwise.
        """
        self._buffer += list(data)
        self._parse(eof)
        token = ''.join(self._token)
        remaining = ''.join(self._buffer)
        self._token, self._remaining = [], []
        return token, remaining
    
    def _parse(self, eof):
        while 1:
            #Look for first unescaped newline. Quotes may be ignored
            escaped = False
            for i,c in enumerate(self._buffer):
                if escaped:
                    escaped = False
                elif c=='\\':
                    escaped = True
                elif c=='\n':
                    break
            else:
                i = -1
                    
            if i==-1 or self._buffer[i]!='\n':
                if not eof:
                    raise NeedMore()
                #No more data, maybe the last line is closing delimiter
                line = ''.join(self._buffer)
                eol = ''
                self._buffer[:] = []
            else:
                line = ''.join(self._buffer[:i])
                eol = self._buffer[i]
                self._buffer[:i+1] = []
            
            if self._op=='<<-':
                line = line.lstrip('\t')
                
            if line==self._delim:
                break
                
            self._token += [line, eol]
            if i==-1:
                break
    
class Token:
    #TODO: check this is still in use
    OPERATOR = 'OPERATOR'
    WORD = 'WORD'
    
    def __init__(self):
        self.value = ''
        self.type = None
        
    def __getitem__(self, key):
        #Behave like a two elements tuple
        if key==0:
            return self.type
        if key==1:
            return self.value
        raise IndexError(key)
               
               
class HereDoc:
    def __init__(self, op, name=None):
        self.op = op
        self.name = name
        self.pendings = []
               
TK_COMMA        = 'COMMA'
TK_AMPERSAND    = 'AMP'
TK_OP           = 'OP'
TK_TOKEN        = 'TOKEN'
TK_COMMENT      = 'COMMENT'
TK_NEWLINE      = 'NEWLINE' 
TK_IONUMBER     = 'IO_NUMBER'
TK_ASSIGNMENT   = 'ASSIGNMENT_WORD'
TK_HERENAME     = 'HERENAME'

class Lexer:
    """Main lexer.
    
    Call add() until the script AST is returned.
    """
    # Here-document handling makes the whole thing more complex because they basically
    # force tokens to be reordered: here-content must come right after the operator
    # and the here-document name, while some other tokens might be following the
    # here-document expression on the same line.
    #
    # So, here-doc states are basically:
    #   *self._state==ST_NORMAL
    #       - self._heredoc.op is None: no here-document
    #       - self._heredoc.op is not None but name is: here-document operator matched,
    #           waiting for the document name/delimiter
    #       - self._heredoc.op and name are not None: here-document is ready, following
    #           tokens are being stored and will be pushed again when the document is
    #           completely parsed.
    #   *self._state==ST_HEREDOC
    #       - The here-document is being delimited by self._herelexer. Once it is done
    #           the content is pushed in front of the pending token list then all these
    #           tokens are pushed once again.
    ST_NORMAL       = 'ST_NORMAL'
    ST_OP           = 'ST_OP'
    ST_BACKSLASH    = 'ST_BACKSLASH'
    ST_QUOTED       = 'ST_QUOTED'
    ST_COMMENT      = 'ST_COMMENT'
    ST_HEREDOC      = 'ST_HEREDOC'
    
    #Match end of backquote strings
    RE_BACKQUOTE_END = re.compile(r'(?<!\\)(`)')

    def __init__(self, parent_state = None):
        self._input = []
        self._pos = 0
        
        self._token = ''
        self._type = TK_TOKEN
        
        self._state = self.ST_NORMAL
        self._parent_state = parent_state
        self._wordlexer = None
        
        self._heredoc = HereDoc(None)
        self._herelexer = None
        
        ### Following attributes are not used for delimiting token and can safely
        ### be changed after here-document detection (see _push_toke)
        
        # Count the number of tokens following a 'For' reserved word. Needed to
        # return an 'In' reserved word if it comes in third place.
        self._for_count = None
        
    def add(self, data, eof=False):
        """Feed the lexer with data.
        
        When eof is set to True, returns unconsumed data or raise if the lexer
        is in the middle of a delimiting operation.
        Raise NeedMore otherwise.
        """
        self._input += list(data)
        self._parse(eof)
        self._input[:self._pos] = []
        return ''.join(self._input)
        
    def _parse(self, eof):            
        while self._state:
            if self._pos>=len(self._input):
                if not eof:
                    raise NeedMore()
                elif self._state not in (self.ST_OP, self.ST_QUOTED, self.ST_HEREDOC):
                    #Delimit the current token and leave cleanly
                    self._push_token('')
                    break
                else:
                    #Let the sublexer handle the eof themselves
                    pass
                
            if self._state==self.ST_NORMAL:
                self._parse_normal()
            elif self._state==self.ST_COMMENT:
                self._parse_comment()
            elif self._state==self.ST_OP:
                self._parse_op(eof)
            elif self._state==self.ST_QUOTED:
                self._parse_quoted(eof)
            elif self._state==self.ST_HEREDOC:
                self._parse_heredoc(eof)
            else:
                assert False, "Unknown state " + str(self._state)
                
        if self._heredoc.op is not None:
            raise ShellSyntaxError('missing here-document delimiter')
                
    def _parse_normal(self):
        c = self._input[self._pos]
        if c=='\n':
            self._push_token(c)
            self._token = c
            self._type = TK_NEWLINE
            self._push_token('')
            self._pos += 1
        elif c in ('\\', '\'', '"', '`', '$'):
            self._state = self.ST_QUOTED
        elif is_partial_op(c):
            self._push_token(c)
            
            self._type = TK_OP
            self._token += c
            self._pos += 1
            self._state = self.ST_OP
        elif is_blank(c):
            self._push_token(c)
            
            #Discard blanks
            self._pos += 1
        elif self._token:
            self._token += c
            self._pos += 1
        elif c=='#':
            self._state = self.ST_COMMENT
            self._type = TK_COMMENT
            self._pos += 1
        else:
            self._pos += 1
            self._token += c          
                
    def _parse_op(self, eof):
        assert self._token
        
        while 1:
            if self._pos>=len(self._input):
                if not eof:
                    raise NeedMore()
                c = ''
            else:                
                c = self._input[self._pos]
                
            op = self._token + c
            if c and is_partial_op(op):
                #Still parsing an operator
                self._token = op
                self._pos += 1
            else:            
                #End of operator
                self._push_token(c)                    
                self._state = self.ST_NORMAL
                break
                
    def _parse_comment(self):
        while 1:
            if self._pos>=len(self._input):
                raise NeedMore()
                
            c = self._input[self._pos]
            if c=='\n':
                #End of comment, do not consume the end of line
                self._state = self.ST_NORMAL
                break
            else:
                self._token += c
                self._pos += 1
                
    def _parse_quoted(self, eof):
        """Precondition: the starting backquote/dollar is still in the input queue."""
        if not self._wordlexer:
            self._wordlexer = WordLexer()
        
        if self._pos<len(self._input):
             #Transfer input queue character into the subparser
            input = self._input[self._pos:]
            self._pos += len(input)
            
        wtree, remaining = self._wordlexer.add(input, eof)
        self._wordlexer = None
        self._token += wordtree_as_string(wtree)
        
        #Put unparsed character back in the input queue
        if remaining:
            self._input[self._pos:self._pos] = list(remaining)          
        self._state = self.ST_NORMAL
        
    def _parse_heredoc(self, eof):
        assert not self._token
        
        if self._herelexer is None:
            self._herelexer = HereDocLexer(self._heredoc.op, self._heredoc.name)
        
        if self._pos<len(self._input):
             #Transfer input queue character into the subparser
            input = self._input[self._pos:]
            self._pos += len(input)
        
        self._token, remaining = self._herelexer.add(input, eof)
        
        #Reset here-document state
        self._herelexer = None
        heredoc, self._heredoc = self._heredoc, HereDoc(None)
        if remaining:
            self._input[self._pos:self._pos] = list(remaining)
        self._state = self.ST_NORMAL
        
        #Push pending tokens
        heredoc.pendings[:0] = [(self._token, self._type, heredoc.name)]
        for token, type, delim in heredoc.pendings:
            self._token = token
            self._type = type
            self._push_token(delim)
                     
    def _push_token(self, delim):
        if not self._token:
            return 0
            
        if self._heredoc.op is not None:
            if self._heredoc.name is None:
                #Here-document name
                if self._type!=TK_TOKEN:
                    raise ShellSyntaxError("expecting here-document name, got '%s'" % self._token)
                self._heredoc.name = unquote_wordtree(make_wordtree(self._token))
                self._type = TK_HERENAME
            else:
                #Capture all tokens until the newline starting the here-document
                if self._type==TK_NEWLINE:
                    assert self._state==self.ST_NORMAL
                    self._state = self.ST_HEREDOC    
                
                self._heredoc.pendings.append((self._token, self._type, delim))    
                self._token = ''
                self._type = TK_TOKEN
                return 1
                
        # BEWARE: do not change parser state from here to the end of the function:
        # when parsing between an here-document operator to the end of the line
        # tokens are stored in self._heredoc.pendings. Therefore, they will not
        # reach the section below.
                    
        #Check operators
        if self._type==TK_OP:
            #False positive because of partial op matching
            op = is_op(self._token)
            if not op:
                self._type = TK_TOKEN
            else:
                #Map to the specific operator
                self._type = op
                if self._token in ('<<', '<<-'):
                    #Done here rather than in _parse_op because there is no need
                    #to change the parser state since we are still waiting for
                    #the here-document name
                    if self._heredoc.op is not None:
                        raise ShellSyntaxError("syntax error near token '%s'" % self._token)
                    assert self._heredoc.op is None
                    self._heredoc.op = self._token
                
        if self._type==TK_TOKEN:            
            if '=' in self._token and not delim:
                if self._token.startswith('='):
                    #Token is a WORD... a TOKEN that is.
                    pass
                else:
                    prev = self._token[:self._token.find('=')]
                    if is_name(prev):
                        self._type = TK_ASSIGNMENT
                    else:
                        #Just a token (unspecified)
                        pass
            else:
                reserved = get_reserved(self._token)
                if reserved is not None:
                    if reserved=='In' and self._for_count!=2:
                        #Sorry, not a reserved word after all
                        pass
                    else:
                        self._type = reserved
                        if reserved in ('For', 'Case'):
                            self._for_count = 0                    
                elif are_digits(self._token) and delim in ('<', '>'):
                    #Detect IO_NUMBER
                    self._type = TK_IONUMBER
                elif self._token==';':
                    self._type = TK_COMMA
                elif self._token=='&':
                    self._type = TK_AMPERSAND
        elif self._type==TK_COMMENT:
            #Comments are not part of sh grammar, ignore them
            self._token = ''
            self._type = TK_TOKEN
            return 0
        
        if self._for_count is not None:
            #Track token count in 'For' expression to detect 'In' reserved words.
            #Can only be in third position, no need to go beyond
            self._for_count += 1
            if self._for_count==3:
                self._for_count = None
                
        self.on_token((self._token, self._type))
        self._token = ''
        self._type = TK_TOKEN
        return 1
                        
    def on_token(self, token):
        raise NotImplementedError
                 

tokens = [
    TK_TOKEN,
# To silence yacc unused token warnings
#    TK_COMMENT,
    TK_NEWLINE,
    TK_IONUMBER,
    TK_ASSIGNMENT,
    TK_HERENAME,
]            

#Add specific operators
tokens += _OPERATORS.values()
#Add reserved words
tokens += _RESERVEDS.values()
            
class PLYLexer(Lexer):
    """Bridge Lexer and PLY lexer interface."""
    def __init__(self):
        Lexer.__init__(self)
        self._tokens = []
        self._current = 0
        self.lineno = 0

    def on_token(self, token):
        value, type = token

        self.lineno = 0
        t = lex.LexToken()
        t.value = value
        t.type = type
        t.lexer = self
        t.lexpos = 0
        t.lineno = 0
        
        self._tokens.append(t)
        
    def is_empty(self):
        return not bool(self._tokens)
        
    #PLY compliant interface
    def token(self):
        if self._current>=len(self._tokens):
            return None
        t = self._tokens[self._current]
        self._current += 1
        return t      
        
        
def get_tokens(s):
    """Parse the input string and return a tuple (tokens, unprocessed) where
    tokens is a list of parsed tokens and unprocessed is the part of the input
    string left untouched by the lexer.
    """
    lexer = PLYLexer()
    untouched = lexer.add(s, True) 
    tokens = []
    while 1:
        token = lexer.token()
        if token is None:
            break
        tokens.append(token)
        
    tokens = [(t.value, t.type) for t in tokens]
    return tokens, untouched        
