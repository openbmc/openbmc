import sys
import argparse
from collections import defaultdict, OrderedDict

class ArgumentUsageError(Exception):
    """Exception class you can raise (and catch) in order to show the help"""
    def __init__(self, message, subcommand=None):
        self.message = message
        self.subcommand = subcommand

class ArgumentParser(argparse.ArgumentParser):
    """Our own version of argparse's ArgumentParser"""
    def __init__(self, *args, **kwargs):
        kwargs.setdefault('formatter_class', OeHelpFormatter)
        self._subparser_groups = OrderedDict()
        super(ArgumentParser, self).__init__(*args, **kwargs)
        self._positionals.title = 'arguments'
        self._optionals.title = 'options'

    def error(self, message):
        """error(message: string)

        Prints a help message incorporating the message to stderr and
        exits.
        """
        self._print_message('%s: error: %s\n' % (self.prog, message), sys.stderr)
        self.print_help(sys.stderr)
        sys.exit(2)

    def error_subcommand(self, message, subcommand):
        if subcommand:
            action = self._get_subparser_action()
            try:
                subparser = action._name_parser_map[subcommand]
            except KeyError:
                self.error('no subparser for name "%s"' % subcommand)
            else:
                subparser.error(message)

        self.error(message)

    def add_subparsers(self, *args, **kwargs):
        if 'dest' not in kwargs:
            kwargs['dest'] = '_subparser_name'

        ret = super(ArgumentParser, self).add_subparsers(*args, **kwargs)
        # Need a way of accessing the parent parser
        ret._parent_parser = self
        # Ensure our class gets instantiated
        ret._parser_class = ArgumentSubParser
        # Hacky way of adding a method to the subparsers object
        ret.add_subparser_group = self.add_subparser_group
        return ret

    def add_subparser_group(self, groupname, groupdesc, order=0):
        self._subparser_groups[groupname] = (groupdesc, order)

    def parse_args(self, args=None, namespace=None):
        """Parse arguments, using the correct subparser to show the error."""
        args, argv = self.parse_known_args(args, namespace)
        if argv:
            message = 'unrecognized arguments: %s' % ' '.join(argv)
            if self._subparsers:
                subparser = self._get_subparser(args)
                subparser.error(message)
            else:
                self.error(message)
            sys.exit(2)
        return args

    def _get_subparser(self, args):
        action = self._get_subparser_action()
        if action.dest == argparse.SUPPRESS:
            self.error('cannot get subparser, the subparser action dest is suppressed')

        name = getattr(args, action.dest)
        try:
            return action._name_parser_map[name]
        except KeyError:
            self.error('no subparser for name "%s"' % name)

    def _get_subparser_action(self):
        if not self._subparsers:
            self.error('cannot return the subparser action, no subparsers added')

        for action in self._subparsers._group_actions:
            if isinstance(action, argparse._SubParsersAction):
                return action


class ArgumentSubParser(ArgumentParser):
    def __init__(self, *args, **kwargs):
        if 'group' in kwargs:
            self._group = kwargs.pop('group')
        if 'order' in kwargs:
            self._order = kwargs.pop('order')
        super(ArgumentSubParser, self).__init__(*args, **kwargs)

    def parse_known_args(self, args=None, namespace=None):
        # This works around argparse not handling optional positional arguments being
        # intermixed with other options. A pretty horrible hack, but we're not left
        # with much choice given that the bug in argparse exists and it's difficult
        # to subclass.
        # Borrowed from http://stackoverflow.com/questions/20165843/argparse-how-to-handle-variable-number-of-arguments-nargs
        # with an extra workaround (in format_help() below) for the positional
        # arguments disappearing from the --help output, as well as structural tweaks.
        # Originally simplified from http://bugs.python.org/file30204/test_intermixed.py
        positionals = self._get_positional_actions()
        for action in positionals:
            # deactivate positionals
            action.save_nargs = action.nargs
            action.nargs = 0

        namespace, remaining_args = super(ArgumentSubParser, self).parse_known_args(args, namespace)
        for action in positionals:
            # remove the empty positional values from namespace
            if hasattr(namespace, action.dest):
                delattr(namespace, action.dest)
        for action in positionals:
            action.nargs = action.save_nargs
        # parse positionals
        namespace, extras = super(ArgumentSubParser, self).parse_known_args(remaining_args, namespace)
        return namespace, extras

    def format_help(self):
        # Quick, restore the positionals!
        positionals = self._get_positional_actions()
        for action in positionals:
            if hasattr(action, 'save_nargs'):
                action.nargs = action.save_nargs
        return super(ArgumentParser, self).format_help()


class OeHelpFormatter(argparse.HelpFormatter):
    def _format_action(self, action):
        if hasattr(action, '_get_subactions'):
            # subcommands list
            groupmap = defaultdict(list)
            ordermap = {}
            subparser_groups = action._parent_parser._subparser_groups
            groups = sorted(subparser_groups.keys(), key=lambda item: subparser_groups[item][1], reverse=True)
            for subaction in self._iter_indented_subactions(action):
                parser = action._name_parser_map[subaction.dest]
                group = getattr(parser, '_group', None)
                groupmap[group].append(subaction)
                if group not in groups:
                    groups.append(group)
                order = getattr(parser, '_order', 0)
                ordermap[subaction.dest] = order

            lines = []
            if len(groupmap) > 1:
                groupindent = '  '
            else:
                groupindent = ''
            for group in groups:
                subactions = groupmap[group]
                if not subactions:
                    continue
                if groupindent:
                    if not group:
                        group = 'other'
                    groupdesc = subparser_groups.get(group, (group, 0))[0]
                    lines.append('  %s:' % groupdesc)
                for subaction in sorted(subactions, key=lambda item: ordermap[item.dest], reverse=True):
                    lines.append('%s%s' % (groupindent, self._format_action(subaction).rstrip()))
            return '\n'.join(lines)
        else:
            return super(OeHelpFormatter, self)._format_action(action)

def int_positive(value):
    ivalue = int(value)
    if ivalue <= 0:
        raise argparse.ArgumentTypeError(
                "%s is not a positive int value" % value)
    return ivalue
