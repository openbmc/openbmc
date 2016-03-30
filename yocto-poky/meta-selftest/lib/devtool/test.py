import argparse

def selftest_reverse(args, config, basepath, workspace):
    """Reverse the value passed to verify the plugin is executing."""
    print args.value[::-1]

def register_commands(subparsers, context):
    parser_build = subparsers.add_parser('selftest-reverse', help='Reverse value (for selftest)',
                                         formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser_build.add_argument('value', help='Value to reverse')
    parser_build.set_defaults(func=selftest_reverse)
