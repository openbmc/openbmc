#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
import argparse
import re

class myArgumentParser(argparse.ArgumentParser):
    def _print_message(self, message, file=None):
        bb.warn("%s - %s: %s" % (d.getVar('PN'), pkg, message))

    # This should never be called...
    def exit(self, status=0, message=None):
        message = message or ("%s - %s: useradd.bbclass: Argument parsing exited" % (d.getVar('PN'), pkg))
        error(message)

    def error(self, message):
        bb.fatal(message)

def split_commands(params):
    params = re.split('''[ \t]*;[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params.strip())
    # Remove any empty items
    return [x for x in params if x]

def split_args(params):
    params = re.split('''[ \t]+(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params.strip())
    # Remove any empty items
    return [x for x in params if x]

def build_useradd_parser():
    # The following comes from --help on useradd from shadow
    parser = myArgumentParser(prog='useradd')
    parser.add_argument("-b", "--base-dir", metavar="BASE_DIR", help="base directory for the home directory of the new account")
    parser.add_argument("-c", "--comment", metavar="COMMENT", help="GECOS field of the new account")
    parser.add_argument("-d", "--home-dir", metavar="HOME_DIR", help="home directory of the new account")
    parser.add_argument("-D", "--defaults", help="print or change default useradd configuration", action="store_true")
    parser.add_argument("-e", "--expiredate", metavar="EXPIRE_DATE", help="expiration date of the new account")
    parser.add_argument("-f", "--inactive", metavar="INACTIVE", help="password inactivity period of the new account")
    parser.add_argument("-g", "--gid", metavar="GROUP", help="name or ID of the primary group of the new account")
    parser.add_argument("-G", "--groups", metavar="GROUPS", help="list of supplementary groups of the new account")
    parser.add_argument("-k", "--skel", metavar="SKEL_DIR", help="use this alternative skeleton directory")
    parser.add_argument("-K", "--key", metavar="KEY=VALUE", help="override /etc/login.defs defaults")
    parser.add_argument("-l", "--no-log-init", help="do not add the user to the lastlog and faillog databases", action="store_true")
    parser.add_argument("-m", "--create-home", help="create the user's home directory", action="store_const", const=True)
    parser.add_argument("-M", "--no-create-home", dest="create_home", help="do not create the user's home directory", action="store_const", const=False)
    parser.add_argument("-N", "--no-user-group", dest="user_group", help="do not create a group with the same name as the user", action="store_const", const=False)
    parser.add_argument("-o", "--non-unique", help="allow to create users with duplicate (non-unique UID)", action="store_true")
    parser.add_argument("-p", "--password", metavar="PASSWORD", help="encrypted password of the new account")
    parser.add_argument("-R", "--root", metavar="CHROOT_DIR", help="directory to chroot into")
    parser.add_argument("-r", "--system", help="create a system account", action="store_true")
    parser.add_argument("-s", "--shell", metavar="SHELL", help="login shell of the new account")
    parser.add_argument("-u", "--uid", metavar="UID", help="user ID of the new account")
    parser.add_argument("-U", "--user-group", help="create a group with the same name as the user", action="store_const", const=True)
    parser.add_argument("LOGIN", help="Login name of the new user")

    return parser

def build_groupadd_parser():
    # The following comes from --help on groupadd from shadow
    parser = myArgumentParser(prog='groupadd')
    parser.add_argument("-f", "--force", help="exit successfully if the group already exists, and cancel -g if the GID is already used", action="store_true")
    parser.add_argument("-g", "--gid", metavar="GID", help="use GID for the new group")
    parser.add_argument("-K", "--key", metavar="KEY=VALUE", help="override /etc/login.defs defaults")
    parser.add_argument("-o", "--non-unique", help="allow to create groups with duplicate (non-unique) GID", action="store_true")
    parser.add_argument("-p", "--password", metavar="PASSWORD", help="use this encrypted password for the new group")
    parser.add_argument("-R", "--root", metavar="CHROOT_DIR", help="directory to chroot into")
    parser.add_argument("-r", "--system", help="create a system account", action="store_true")
    parser.add_argument("GROUP", help="Group name of the new group")

    return parser
