from __future__ import absolute_import
import inspect
import traceback
import bb.namedtuple_with_abc
from collections import namedtuple


class TracebackEntry(namedtuple.abc):
    """Pickleable representation of a traceback entry"""
    _fields = 'filename lineno function args code_context index'
    _header = '  File "{0.filename}", line {0.lineno}, in {0.function}{0.args}'

    def format(self, formatter=None):
        if not self.code_context:
            return self._header.format(self) + '\n'

        formatted = [self._header.format(self) + ':\n']

        for lineindex, line in enumerate(self.code_context):
            if formatter:
                line = formatter(line)

            if lineindex == self.index:
                formatted.append('    >%s' % line)
            else:
                formatted.append('     %s' % line)
        return formatted

    def __str__(self):
        return ''.join(self.format())

def _get_frame_args(frame):
    """Get the formatted arguments and class (if available) for a frame"""
    arginfo = inspect.getargvalues(frame)

    try:
        if not arginfo.args:
            return '', None
    # There have been reports from the field of python 2.6 which doesn't 
    # return a namedtuple here but simply a tuple so fallback gracefully if
    # args isn't present.
    except AttributeError:
        return '', None

    firstarg = arginfo.args[0]
    if firstarg == 'self':
        self = arginfo.locals['self']
        cls = self.__class__.__name__

        arginfo.args.pop(0)
        del arginfo.locals['self']
    else:
        cls = None

    formatted = inspect.formatargvalues(*arginfo)
    return formatted, cls

def extract_traceback(tb, context=1):
    frames = inspect.getinnerframes(tb, context)
    for frame, filename, lineno, function, code_context, index in frames:
        formatted_args, cls = _get_frame_args(frame)
        if cls:
            function = '%s.%s' % (cls, function)
        yield TracebackEntry(filename, lineno, function, formatted_args,
                             code_context, index)

def format_extracted(extracted, formatter=None, limit=None):
    if limit:
        extracted = extracted[-limit:]

    formatted = []
    for tracebackinfo in extracted:
        formatted.extend(tracebackinfo.format(formatter))
    return formatted


def format_exception(etype, value, tb, context=1, limit=None, formatter=None):
    formatted = ['Traceback (most recent call last):\n']

    if hasattr(tb, 'tb_next'):
        tb = extract_traceback(tb, context)

    formatted.extend(format_extracted(tb, formatter, limit))
    formatted.extend(traceback.format_exception_only(etype, value))
    return formatted

def to_string(exc):
    if isinstance(exc, SystemExit):
        if not isinstance(exc.code, basestring):
            return 'Exited with "%d"' % exc.code
    return str(exc)
