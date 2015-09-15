# In order to support a deterministic set of 'dynamic' users/groups,
# we need a function to reformat the params based on a static file
def update_useradd_static_config(d):
    import argparse
    import re

    class myArgumentParser( argparse.ArgumentParser ):
        def _print_message(self, message, file=None):
            bb.warn("%s - %s: %s" % (d.getVar('PN', True), pkg, message))

        # This should never be called...
        def exit(self, status=0, message=None):
            message = message or ("%s - %s: useradd.bbclass: Argument parsing exited" % (d.getVar('PN', True), pkg))
            error(message)

        def error(self, message):
            raise bb.build.FuncFailed(message)

    # We parse and rewrite the useradd components
    def rewrite_useradd(params):
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
        parser.add_argument("-m", "--create-home", help="create the user's home directory", action="store_true")
        parser.add_argument("-M", "--no-create-home", help="do not create the user's home directory", action="store_true")
        parser.add_argument("-N", "--no-user-group", help="do not create a group with the same name as the user", action="store_true")
        parser.add_argument("-o", "--non-unique", help="allow to create users with duplicate (non-unique UID)", action="store_true")
        parser.add_argument("-p", "--password", metavar="PASSWORD", help="encrypted password of the new account")
        parser.add_argument("-R", "--root", metavar="CHROOT_DIR", help="directory to chroot into")
        parser.add_argument("-r", "--system", help="create a system account", action="store_true")
        parser.add_argument("-s", "--shell", metavar="SHELL", help="login shell of the new account")
        parser.add_argument("-u", "--uid", metavar="UID", help="user ID of the new account")
        parser.add_argument("-U", "--user-group", help="create a group with the same name as the user", action="store_true")
        parser.add_argument("LOGIN", help="Login name of the new user")

        # Return a list of configuration files based on either the default
        # files/passwd or the contents of USERADD_UID_TABLES
        # paths are resulved via BBPATH
        def get_passwd_list(d):
            str = ""
            bbpath = d.getVar('BBPATH', True)
            passwd_tables = d.getVar('USERADD_UID_TABLES', True)
            if not passwd_tables:
                passwd_tables = 'files/passwd'
            for conf_file in passwd_tables.split():
                str += " %s" % bb.utils.which(bbpath, conf_file)
            return str

        newparams = []
        for param in re.split('''[ \t]*;[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params):
            param = param.strip()
            if not param:
                continue
            try:
                uaargs = parser.parse_args(re.split('''[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', param))
            except:
                raise bb.build.FuncFailed("%s: Unable to parse arguments for USERADD_PARAM_%s: '%s'" % (d.getVar('PN', True), pkg, param))

            # files/passwd or the contents of USERADD_UID_TABLES
            # Use the standard passwd layout:
            #  username:password:user_id:group_id:comment:home_directory:login_shell
            # (we want to process in reverse order, as 'last found' in the list wins)
            #
            # If a field is left blank, the original value will be used.  The 'username'
            # field is required.
            #
            # Note: we ignore the password field, as including even the hashed password
            # in the useradd command may introduce a security hole.  It's assumed that
            # all new users get the default ('*' which prevents login) until the user is
            # specifically configured by the system admin.
            for conf in get_passwd_list(d).split()[::-1]:
                if os.path.exists(conf):
                    f = open(conf, "r")
                    for line in f:
                        if line.startswith('#'):
                            continue
                        field = line.rstrip().split(":")
                        if field[0] == uaargs.LOGIN:
                            if uaargs.uid and field[2] and (uaargs.uid != field[2]):
                                bb.warn("%s: Changing username %s's uid from (%s) to (%s), verify configuration files!" % (d.getVar('PN', True), uaargs.LOGIN, uaargs.uid, field[2]))
                            uaargs.uid = [field[2], uaargs.uid][not field[2]]

                            # Determine the possible groupname
                            # Unless the group name (or gid) is specified, we assume that the LOGIN is the groupname
                            #
                            # By default the system has creation of the matching groups enabled
                            # So if the implicit username-group creation is on, then the implicit groupname (LOGIN)
                            # is used, and we disable the user_group option.
                            #
                            uaargs.groupname = [uaargs.gid, uaargs.LOGIN][not uaargs.gid or uaargs.user_group]
                            uaargs.groupid = [uaargs.gid, uaargs.groupname][not uaargs.gid]
                            uaargs.groupid = [field[3], uaargs.groupid][not field[3]]

                            if not uaargs.gid or uaargs.gid != uaargs.groupid:
                                if (uaargs.groupid and uaargs.groupid.isdigit()) and (uaargs.groupname and uaargs.groupname.isdigit()) and (uaargs.groupid != uaargs.groupname):
                                    # We want to add a group, but we don't know it's name... so we can't add the group...
                                    # We have to assume the group has previously been added or we'll fail on the adduser...
                                    # Note: specifying the actual gid is very rare in OE, usually the group name is specified.
                                    bb.warn("%s: Changing gid for login %s from (%s) to (%s), verify configuration files!" % (d.getVar('PN', True), uaargs.LOGIN, uaargs.groupname, uaargs.gid))
                                elif (uaargs.groupid and not uaargs.groupid.isdigit()) and uaargs.groupid == uaargs.groupname:
                                    # We don't have a number, so we have to add a name
                                    bb.debug(1, "Adding group %s!" % (uaargs.groupname))
                                    uaargs.gid = uaargs.groupid
                                    uaargs.user_group = False
                                    groupadd = d.getVar("GROUPADD_PARAM_%s" % pkg, True)
                                    newgroup = "%s %s" % (['', ' --system'][uaargs.system], uaargs.groupname)
                                    if groupadd:
                                        d.setVar("GROUPADD_PARAM_%s" % pkg, "%s ; %s" % (groupadd, newgroup))
                                    else:
                                        d.setVar("GROUPADD_PARAM_%s" % pkg, newgroup)
                                elif uaargs.groupname and (uaargs.groupid and uaargs.groupid.isdigit()):
                                    # We have a group name and a group number to assign it to
                                    bb.debug(1, "Adding group %s  gid (%s)!" % (uaargs.groupname, uaargs.groupid))
                                    uaargs.gid = uaargs.groupid
                                    uaargs.user_group = False
                                    groupadd = d.getVar("GROUPADD_PARAM_%s" % pkg, True)
                                    newgroup = "-g %s %s" % (uaargs.gid, uaargs.groupname)
                                    if groupadd:
                                        d.setVar("GROUPADD_PARAM_%s" % pkg, "%s ; %s" % (groupadd, newgroup))
                                    else:
                                        d.setVar("GROUPADD_PARAM_%s" % pkg, newgroup)

                            uaargs.comment = ["'%s'" % field[4], uaargs.comment][not field[4]]
                            uaargs.home_dir = [field[5], uaargs.home_dir][not field[5]]
                            uaargs.shell = [field[6], uaargs.shell][not field[6]]
                            break

            # Should be an error if a specific option is set...
            if d.getVar('USERADD_ERROR_DYNAMIC', True) == '1' and not ((uaargs.uid and uaargs.uid.isdigit()) and uaargs.gid):
                #bb.error("Skipping recipe %s, package %s which adds username %s does not have a static uid defined." % (d.getVar('PN', True),  pkg, uaargs.LOGIN))
                raise bb.build.FuncFailed("%s - %s: Username %s does not have a static uid defined." % (d.getVar('PN', True), pkg, uaargs.LOGIN))

            # Reconstruct the args...
            newparam  = ['', ' --defaults'][uaargs.defaults]
            newparam += ['', ' --base-dir %s' % uaargs.base_dir][uaargs.base_dir != None]
            newparam += ['', ' --comment %s' % uaargs.comment][uaargs.comment != None]
            newparam += ['', ' --home-dir %s' % uaargs.home_dir][uaargs.home_dir != None]
            newparam += ['', ' --expiredata %s' % uaargs.expiredate][uaargs.expiredate != None]
            newparam += ['', ' --inactive %s' % uaargs.inactive][uaargs.inactive != None]
            newparam += ['', ' --gid %s' % uaargs.gid][uaargs.gid != None]
            newparam += ['', ' --groups %s' % uaargs.groups][uaargs.groups != None]
            newparam += ['', ' --skel %s' % uaargs.skel][uaargs.skel != None]
            newparam += ['', ' --key %s' % uaargs.key][uaargs.key != None]
            newparam += ['', ' --no-log-init'][uaargs.no_log_init]
            newparam += ['', ' --create-home'][uaargs.create_home]
            newparam += ['', ' --no-create-home'][uaargs.no_create_home]
            newparam += ['', ' --no-user-group'][uaargs.no_user_group]
            newparam += ['', ' --non-unique'][uaargs.non_unique]
            newparam += ['', ' --password %s' % uaargs.password][uaargs.password != None]
            newparam += ['', ' --root %s' % uaargs.root][uaargs.root != None]
            newparam += ['', ' --system'][uaargs.system]
            newparam += ['', ' --shell %s' % uaargs.shell][uaargs.shell != None]
            newparam += ['', ' --uid %s' % uaargs.uid][uaargs.uid != None]
            newparam += ['', ' --user-group'][uaargs.user_group]
            newparam += ' %s' % uaargs.LOGIN

            newparams.append(newparam)

        return " ;".join(newparams).strip()

    # We parse and rewrite the groupadd components
    def rewrite_groupadd(params):
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

        # Return a list of configuration files based on either the default
        # files/group or the contents of USERADD_GID_TABLES
        # paths are resulved via BBPATH
        def get_group_list(d):
            str = ""
            bbpath = d.getVar('BBPATH', True)
            group_tables = d.getVar('USERADD_GID_TABLES', True)
            if not group_tables:
                group_tables = 'files/group'
            for conf_file in group_tables.split():
                str += " %s" % bb.utils.which(bbpath, conf_file)
            return str

        newparams = []
        for param in re.split('''[ \t]*;[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params):
            param = param.strip()
            if not param:
                continue
            try:
                # If we're processing multiple lines, we could have left over values here...
                gaargs = parser.parse_args(re.split('''[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', param))
            except:
                raise bb.build.FuncFailed("%s: Unable to parse arguments for GROUPADD_PARAM_%s: '%s'" % (d.getVar('PN', True), pkg, param))

            # Need to iterate over layers and open the right file(s)
            # Use the standard group layout:
            #  groupname:password:group_id:group_members
            #
            # If a field is left blank, the original value will be used. The 'groupname' field
            # is required.
            #
            # Note: similar to the passwd file, the 'password' filed is ignored
            # Note: group_members is ignored, group members must be configured with the GROUPMEMS_PARAM
            for conf in get_group_list(d).split()[::-1]:
                if os.path.exists(conf):
                    f = open(conf, "r")
                    for line in f:
                        if line.startswith('#'):
                            continue
                        field = line.rstrip().split(":")
                        if field[0] == gaargs.GROUP and field[2]:
                            if gaargs.gid and (gaargs.gid != field[2]):
                                bb.warn("%s: Changing groupname %s's gid from (%s) to (%s), verify configuration files!" % (d.getVar('PN', True), gaargs.GROUP, gaargs.gid, field[2]))
                            gaargs.gid = field[2]
                            break

            if d.getVar('USERADD_ERROR_DYNAMIC', True) == '1' and not (gaargs.gid and gaargs.gid.isdigit()):
                #bb.error("Skipping recipe %s, package %s which adds groupname %s does not have a static gid defined." % (d.getVar('PN', True),  pkg, gaargs.GROUP))
                raise bb.build.FuncFailed("%s - %s: Groupname %s does not have a static gid defined." % (d.getVar('PN', True), pkg, gaargs.GROUP))

            # Reconstruct the args...
            newparam  = ['', ' --force'][gaargs.force]
            newparam += ['', ' --gid %s' % gaargs.gid][gaargs.gid != None]
            newparam += ['', ' --key %s' % gaargs.key][gaargs.key != None]
            newparam += ['', ' --non-unique'][gaargs.non_unique]
            newparam += ['', ' --password %s' % gaargs.password][gaargs.password != None]
            newparam += ['', ' --root %s' % gaargs.root][gaargs.root != None]
            newparam += ['', ' --system'][gaargs.system]
            newparam += ' %s' % gaargs.GROUP

            newparams.append(newparam)

        return " ;".join(newparams).strip()

    # Load and process the users and groups, rewriting the adduser/addgroup params
    useradd_packages = d.getVar('USERADD_PACKAGES', True)

    for pkg in useradd_packages.split():
        # Groupmems doesn't have anything we might want to change, so simply validating
        # is a bit of a waste -- only process useradd/groupadd
        useradd_param = d.getVar('USERADD_PARAM_%s' % pkg, True)
        if useradd_param:
            #bb.warn("Before: 'USERADD_PARAM_%s' - '%s'" % (pkg, useradd_param))
            d.setVar('USERADD_PARAM_%s' % pkg, rewrite_useradd(useradd_param))
            #bb.warn("After:  'USERADD_PARAM_%s' - '%s'" % (pkg, d.getVar('USERADD_PARAM_%s' % pkg, True)))

        groupadd_param = d.getVar('GROUPADD_PARAM_%s' % pkg, True)
        if groupadd_param:
            #bb.warn("Before: 'GROUPADD_PARAM_%s' - '%s'" % (pkg, groupadd_param))
            d.setVar('GROUPADD_PARAM_%s' % pkg, rewrite_groupadd(groupadd_param))
            #bb.warn("After:  'GROUPADD_PARAM_%s' - '%s'" % (pkg, d.getVar('GROUPADD_PARAM_%s' % pkg, True)))



python __anonymous() {
    if not bb.data.inherits_class('nativesdk', d) \
        and not bb.data.inherits_class('native', d):
        try:
            update_useradd_static_config(d)
        except bb.build.FuncFailed as f:
            bb.debug(1, "Skipping recipe %s: %s" % (d.getVar('PN', True), f))
            raise bb.parse.SkipPackage(f)
}
