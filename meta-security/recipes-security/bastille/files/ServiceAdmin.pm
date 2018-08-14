package Bastille::API::ServiceAdmin;
use strict;

use Bastille::API;

use Bastille::API::HPSpecific;
use Bastille::API::FileContent;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT_OK = qw(
B_chkconfig_on
B_chkconfig_off
B_service_start
B_service_stop
B_service_restart
B_is_service_off
checkServiceOnLinux
remoteServiceCheck
remoteNISPlusServiceCheck
B_create_nsswitch_file
);
our @EXPORT = @EXPORT_OK;


#######
# &B_chkconfig_on and &B_chkconfig_off() are great for systems that didn't use
# a more modern init system.  This is a bit of a problem on Fedora, though,
# which used upstart from Fedora 9 to Fedora 14, then switched to a new
# Red Hat-created system called systemd for Fedora 15 and 16 (so far).
# OpenSUSE also moved to systemd, starting with 12.1.  Version 11.4 did not
# use systemd.
# It is also a problem on Ubuntu, starting at version 6.10, where they also
# used upstart.
#####




###########################################################################
# &B_chkconfig_on ($daemon_name) creates the symbolic links that are
# named in the "# chkconfig: ___ _ _ " portion of the init.d files.  We
# need this utility, in place of the distro's chkconfig, because of both
# our need to add revert functionality and our need to harden distros that
# are not mounted on /.
#
# It uses the following global variables to find the links and the init
# scripts, respectively:
#
#   &getGlobal('DIR', "rcd")    -- directory where the rc_.d subdirs can be found
#   &getGlobal('DIR', "initd")  -- directory the rc_.d directories link to
#
# Here an example of where you might use this:
#
# You'd like to tell the system to run the firewall at boot:
#       B_chkconfig_on("bastille-firewall")
#
###########################################################################

# PW: Blech. Copied B_chkconfig_off() and changed a few things,
#               then changed a few more things....

sub B_chkconfig_on {

    my $startup_script=$_[0];
    my $retval=1;

    my $chkconfig_line;
    my ($runlevelinfo,@runlevels);
    my ($start_order,$stop_order,$filetolink);

    &B_log("ACTION","# chkconfig_on enabling $startup_script\n");

    # In Debian system there is no chkconfig script, run levels are checked
    # one by one (jfs)
    if (&GetDistro =~/^DB.*/) {
            $filetolink = &getGlobal('DIR', "initd") . "/$startup_script";
            if (-x $filetolink)
            {
                    foreach my $level ("0","1","2","3","4","5","6" ) {
                            my $link = '';
                            $link = &getGlobal('DIR', "rcd") . "/rc" . "$level" . ".d/K50" . "$startup_script";
                            $retval=symlink($filetolink,$link);
                    }
            }
            return $retval;
    }
    #
    # On SUSE, chkconfig-based rc scripts have been replaced with a whole different
    # system.  chkconfig on SUSE is actually a shell script that does some stuff and then
    # calls insserv, their replacement.
    #

    if (&GetDistro =~ /^SE/) {
        # only try to chkconfig on if init script is found
        if ( -e (&getGlobal('DIR', "initd") . "/$startup_script") ) {
            $chkconfig_line=&getGlobal('BIN','chkconfig');
            &B_System("$chkconfig_line $startup_script on", "$chkconfig_line $startup_script off");
            # chkconfig doesn't take affect until reboot, need to restart service also
            B_service_restart("$startup_script");
            return 1; #success
        }
        return 0; #failure
    }

    #
    # Run through the init script looking for the chkconfig line...
    #
    $retval = open CHKCONFIG,&getGlobal('DIR', "initd") . "/$startup_script";
    unless ($retval) {
        &B_log("ACTION","# Didn't chkconfig_on $startup_script because we couldn't open " . &getGlobal('DIR', "initd") . "/$startup_script\n");
    }
    else {

      READ_LOOP:
        while (my $line=<CHKCONFIG>) {

            # We're looking for lines like this one:
            #      # chkconfig: 2345 10 90
            # OR this
            #      # chkconfig: - 10 90

            if ($line =~ /^#\s*chkconfig:\s*([-\d]+)\s*(\d+)\s*(\d+)/ ) {
                $runlevelinfo = $1;
                $start_order = $2;
                $stop_order = $3;
                # handle a run levels arg of '-'
                if ( $runlevelinfo eq '-' ) {
                    &B_log("ACTION","chkconfig_on saw '-' for run levels for \"$startup_script\", is defaulting to levels 3,4,5\n");
                    $runlevelinfo = '345';
                }
                @runlevels = split(//,$runlevelinfo);
                # make sure the orders have 2 digits
                $start_order =~ s/^(\d)$/0$1/;
                $stop_order =~ s/^(\d)$/0$1/;
                last READ_LOOP;
            }
        }
        close CHKCONFIG;

        # Do we have what we need?
        if ( (scalar(@runlevels) < 1) || (! $start_order =~ /^\d{2}$/) || (! $stop_order =~ /^\d{2}$/) ) {
                # problem
                &B_log("ERROR","# B_chkconfig_on $startup_script failed -- no valid run level/start/stop info found\n");
                return(-1);
        }

        # Now, run through creating symlinks...
        &B_log("ACTION","# chkconfig_on will use run levels ".join(",",@runlevels)." for \"$startup_script\" with S order $start_order and K order $stop_order\n");

        $retval=0;
        # BUG: we really ought to readdir() on &getGlobal('DIR', "rcd") to get all levels
        foreach my $level ( "0","1","2","3","4","5","6" ) {
                my $link = '';
                # we make K links in run levels not specified in the chkconfig line
                $link = &getGlobal('DIR', "rcd") . "/rc" . $level . ".d/K$stop_order" . $startup_script;
                my $klink = $link;
                # now we see if this is a specified run level; if so, make an S link
                foreach my $markedlevel ( @runlevels ) {
                        if ( $level == $markedlevel) {
                                $link = &getGlobal('DIR', "rcd") . "/rc" . $level . ".d/S$start_order" . $startup_script;
                        }
                }
                my $target = &getGlobal('DIR', "initd") ."/" . $startup_script;
                my $local_return;

                if ( (-e "$klink") && ($klink ne $link) ) {
                    # there's a K link, but this level needs an S link
                    unless ($GLOBAL_LOGONLY) {
                        $local_return = unlink("$klink");
                        if ( ! $local_return ) {
                            # unlinking old, bad $klink failed
                            &B_log("ERROR","Unlinking $klink failed\n");
                        } else {
                            &B_log("ACTION","Removed link $klink\n");
                            # If we removed the link, add a link command to the revert file
                            &B_revert_log (&getGlobal('BIN','ln') . " -s $target $klink\n");
                        } # close what to do if unlink works
                    }   # if not GLOBAL_LOGONLY
                }       # if $klink exists and ne $link

                # OK, we've disposed of any old K links, make what we need
                if ( (! ( -e "$link" )) && ($link ne '') ) {
                    # link doesn't exist and the start/stop number is OK; make it
                    unless ($GLOBAL_LOGONLY) {
                        # create the link
                        $local_return = &B_symlink($target,$link);
                        if ($local_return) {
                            $retval++;
                            &B_log("ACTION","Created link $link\n");
                        } else {
                            &B_log("ERROR","Couldn't create $link when trying to chkconfig on $startup_script\n");
                        }
                    }

                } # link doesn't exist
            } # foreach level

    }

    if ($retval < @runlevels) {
        $retval=0;
    }

    $retval;

}


###########################################################################
# &B_chkconfig_off ($daemon_name) deletes the symbolic links that are
# named in the "# chkconfig: ___ _ _ " portion of the init.d files.  We
# need this utility, in place of the distro's chkconfig, because of both
# our need to add revert functionality and our need to harden distros that
# are not mounted on /.
#
# chkconfig allows for a REVERT of its work by writing to an executable
# file &getGlobal('BFILE', "removed-symlinks").
#
# It uses the following global variables to find the links and the init
# scripts, respectively:
#
#   &getGlobal('DIR', "rcd")    -- directory where the rc_.d subdirs can be found
#   &getGlobal('DIR', "initd")  -- directory the rc_.d directories link to
#
# Here an example of where you might use this:
#
# You'd like to tell stop running sendmail in daemon mode on boot:
#       B_chkconfig_off("sendmail")
#
###########################################################################



sub B_chkconfig_off {

    my $startup_script=$_[0];
    my $retval=1;

    my $chkconfig_line;
    my @runlevels;
    my ($start_order,$stop_order,$filetolink);

    if (&GetDistro =~/^DB.*/) {
            $filetolink = &getGlobal('DIR', "initd") . "/$startup_script";
            if (-x $filetolink)
            {
                    # Three ways to do this in Debian:
                    # 1.- have the initd script set to 600 mode
                    # 2.- Remove the links in rcd (re-installing the package
                    # will break it)
                    # 3.- Use update-rc.d --remove (same as 2.)
                    # (jfs)
                    &B_chmod(0600,$filetolink);
                    $retval=6;

                    # The second option
                    #foreach my $level ("0","1","2","3","4","5","6" ) {
                    #my $link = '';
                    #$link = &getGlobal('DIR', "rcd") . "/rc" . "$level" . ".d/K50" . "$startup_script";
                    #unlink($link);
                    #}
            }
    }

    #
    # On SUSE, chkconfig-based rc scripts have been replaced with a whole different
    # system.  chkconfig on SUSE is actually a shell script that does some stuff and then
    # calls insserv, their replacement.
    #
    elsif (&GetDistro =~ /^SE/) {
        # only try to chkconfig off if init script is found
        if ( -e (&getGlobal('DIR', "initd") . "/$startup_script") ) {
            $chkconfig_line=&getGlobal('BIN','chkconfig');
            &B_System("$chkconfig_line $startup_script on", "$chkconfig_line $startup_script off");
            # chkconfig doesn't take affect until reboot, need to stop service
            # since expectation is that the daemons are disabled even without a reboot
            B_service_stop("$startup_script");
            return 1; #success
        }
        return 0; #failure
    }
    else {

            # Run through the init script looking for the chkconfig line...


            $retval = open CHKCONFIG,&getGlobal('DIR', "initd") . "/$startup_script";
            unless ($retval) {
                    &B_log("ACTION","Didn't chkconfig_off $startup_script because we couldn't open " . &getGlobal('DIR', "initd") . "/$startup_script\n");
            }
            else {

                    READ_LOOP:
                    while (my $line=<CHKCONFIG>) {

                            # We're looking for lines like this one:
                            #      # chkconfig: 2345 10 90

                            if ($line =~ /^#\s*chkconfig:\s*([-\d]+)\s*(\d+)\s*(\d+)/ ) {
                                    @runlevels=split //,$1;
                                    $start_order=$2;
                                    $stop_order=$3;


                                    # Change single digit run levels to double digit -- otherwise,
                                    # the alphabetic ordering chkconfig depends on fails.
                                    if ($start_order =~ /^\d$/ ) {
                                            $start_order = "0" . $start_order;
                                            &B_log("ACTION","chkconfig_off converted start order to $start_order\n");
                                    }
                                    if ($stop_order =~ /^\d$/ ) {
                                            $stop_order = "0" . $stop_order;
                                            &B_log("ACTION","chkconfig_off converted stop order to $stop_order\n");
                                    }

                                    last READ_LOOP;
                            }
                    }
                    close CHKCONFIG;

                    # If we never found a chkconfig line, can we just run through all 5
                    # rcX.d dirs from 1 to 5...?

                    # unless ( $start_order and $stop_order ) {
                    #    @runlevels=("1","2","3","4","5");
                    #    $start_order = "*"; $stop_order="*";
                    # }

                    # Now, run through removing symlinks...



                    $retval=0;

                    # Handle the special case that the run level specified is solely "-"
                    if ($runlevels[0] =~ /-/) {
                            @runlevels = ( "0","1","2","3","4","5","6" );
                    }

                    foreach my $level ( @runlevels ) {
                            my $link = &getGlobal('DIR', "rcd") . "/rc" . $level . ".d/S$start_order" . $startup_script;
                            my $new_link = &getGlobal('DIR', "rcd") . "/rc" . $level . ".d/K$stop_order" . $startup_script;
                            my $target = &getGlobal('DIR', "initd") ."/" . $startup_script;
                            my $local_return;


                            # Replace the S__ link in this level with a K__ link.
                            if ( -e $link ) {
                                    unless ($GLOBAL_LOGONLY) {
                                            $local_return=unlink $link;
                                            if ($local_return) {
                                                    $local_return=symlink $target,$new_link;
                                                    unless ($local_return) {
                                                            &B_log("ERROR","Linking $target to $new_link failed.\n");
                                                    }
                                            }
                                            else {  # unlinking failed
                                                    &B_log("ERROR","Unlinking $link failed\n");
                                            }

                                    }
                                    if ($local_return) {
                                            $retval++;
                                            &B_log("ACTION","Removed link $link\n");

                                            #
                                            # If we removed the link, add a link command to the revert file
                                            # Write out the revert information for recreating the S__
                                            # symlink and deleting the K__ symlink.
                                            &B_revert_log(&getGlobal('BIN',"ln") . " -s $target $link\n");
                                            &B_revert_log(&getGlobal('BIN',"rm") . " -f $new_link\n");
                                    }
                                    else {
                                            &B_log("ERROR","B_chkconfig_off $startup_script failed\n");
                                    }

                            }
                    } # foreach

            } # else-unless

    } # else-DB
    if ($retval < @runlevels) {
            $retval=0;
    }

    $retval;

}


###########################################################################
# &B_service_start ($daemon_name)
# Starts service on RedHat/SUSE-based Linux distributions which have the
# service command:
#
#       service $daemon_name start
#
# Other Linux distros that also support this method of starting
# services can be added to use this function.
#
# Here an example of where you might use this:
#
# You'd like to tell the system to start the vsftpd daemon:
#       &B_service_start("vsftpd")
#
# Uses &B_System in HP_API.pm
# To match how the &B_System command works this method:
# returns 1 on success
# returns 0 on failure
###########################################################################

sub B_service_start {

    my $daemon=$_[0];

    if ( (&GetDistro !~ /^SE/) and (&GetDistro !~ /^RH/) and
        (&GetDistro !~ /^RHFC/) and (&GetDistro !~ /^MN/) ) {
        &B_log("ERROR","Tried to call service_start on a system lacking a service command! Internal Bastille error.");
       return undef;
    }

    # only start service if init script is found
    if ( -e (&getGlobal('DIR', 'initd') . "/$daemon") ) {
        &B_log("ACTION","# service_start enabling $daemon\n");

        my $service_cmd=&getGlobal('BIN', 'service');
        if ($service_cmd) {
            # Start the service,
            # Also provide &B_System revert command

            return (&B_System("$service_cmd $daemon start",
                              "$service_cmd $daemon stop"));
        }
    }

    # init script not found, do not try to start, return failure
    return 0;
}

###########################################################################
# &B_service_stop ($daemon_name)
# Stops service on RedHat/SUSE-based Linux distributions which have the
# service command:
#
#       service $daemon_name stop
#
# Other Linux distros that also support this method of starting
# services can be added to use this function.
# Stops service.
#
#
# Here an example of where you might use this:
#
# You'd like to tell the system to stop the vsftpd daemon:
#       &B_service_stop("vsftpd")
#
# Uses &B_System in HP_API.pm
# To match how the &B_System command works this method:
# returns 1 on success
# returns 0 on failure
###########################################################################

sub B_service_stop {

    my $daemon=$_[0];

    if ( (&GetDistro !~ /^SE/) and (&GetDistro !~ /^RH/) and
        (&GetDistro !~ /^RHFC/) and (&GetDistro !~ /^MN/) ) {
        &B_log("ERROR","Tried to call service_stop on a system lacking a service command! Internal Bastille error.");
       return undef;
    }

    # only stop service if init script is found
    if ( -e (&getGlobal('DIR', 'initd') . "/$daemon") ) {
        &B_log("ACTION","# service_stop disabling $daemon\n");

        my $service_cmd=&getGlobal('BIN', 'service');
        if ($service_cmd) {

        # Stop the service,
        # Also provide &B_System revert command

           return (&B_System("$service_cmd $daemon stop",
                             "$service_cmd $daemon start"));
        }
    }

    # init script not found, do not try to stop, return failure
    return 0;
}


###########################################################################
# &B_service_restart ($daemon_name)
# Restarts service on RedHat/SUSE-based Linux distributions which have the
# service command:
#
#       service $daemon_name restart
#
# Other Linux distros that also support this method of starting
# services can be added to use this function.
#
# Here an example of where you might use this:
#
# You'd like to tell the system to restart the vsftpd daemon:
#       &B_service_restart("vsftpd")
#
# Uses &B_System in HP_API.pm
# To match how the &B_System command works this method:
# returns 1 on success
# returns 0 on failure
###########################################################################

sub B_service_restart {

    my $daemon=$_[0];

    if ( (&GetDistro !~ /^SE/) and (&GetDistro !~ /^RH/) and
        (&GetDistro !~ /^RHFC/) and (&GetDistro !~ /^MN/) ) {
        &B_log("ERROR","Tried to call service_restart on a system lacking a service command! Internal Bastille error.");
       return undef;
    }

    # only restart service if init script is found
    if ( -e (&getGlobal('DIR', 'initd') . "/$daemon") ) {
        &B_log("ACTION","# service_restart re-enabling $daemon\n");

        my $service_cmd=&getGlobal('BIN', 'service');
        if ($service_cmd) {

            # Restart the service
            return (&B_System("$service_cmd $daemon restart",
                              "$service_cmd $daemon restart"));
        }
    }

    # init script not found, do not try to restart, return failure
    return 0;
}

###########################################################################
# &B_is_service_off($;$)
#
# Runs the specified test to determine whether or not the question should
# be answered.
#
# return values:
# NOTSECURE_CAN_CHANGE()/0:     service is on
# SECURE_CANT_CHANGE()/1:     service is off
# undef: test is not defined
###########################################################################

sub B_is_service_off ($){
   my $service=$_[0];

   if(&GetDistro =~ "^HP-UX"){
     #die "Why do I think I'm on HPUX?!\n";
     return &checkServiceOnHPUX($service);
   }
   elsif ( (&GetDistro =~ "^RH") || (&GetDistro =~ "^SE") ) {
     return &checkServiceOnLinux($service);
   }
   else {
    &B_log("DEBUG","B_is_service off called for unsupported OS");
     # not yet implemented for other distributions of Linux
     # when GLOBAL_SERVICE, GLOBAL_SERVTYPE and GLOBAL_PROCESS are filled
     # in for Linux, then
     # at least inetd and inittab services should be similar to the above,
     # whereas chkconfig would be used on some Linux distros to determine
     # if non-inetd/inittab services are running at boot time.  Looking at
     # processes should be similar.
     return undef;
   }
}

###########################################################################
# &checkServiceOnLinux($service);
#
# Checks if the given service is running on a Linux system.  This is
# called by B_is_Service_Off(), which is the function that Bastille
# modules should call.
#
# Return values:
# NOTSECURE_CAN_CHANGE() if the service is on
# SECURE_CANT_CHANGE() if the service is off
# undef if the state of the service cannot be determined
#
###########################################################################
sub checkServiceOnLinux($) {
  my $service=$_[0];

  # get the list of parameters which could be used to initiate the service
  # (could be in /etc/rc.d/rc?.d, /etc/inetd.conf, or /etc/inittab, so we
  # check all of them)
  
  my @params = @{ &getGlobal('SERVICE', $service) };
  my $chkconfig = &getGlobal('BIN', 'chkconfig');
  my $grep = &getGlobal('BIN', 'grep');
  my $inittab = &getGlobal('FILE', 'inittab');
  my $serviceType = &getGlobal('SERVTYPE', $service);;

  # A kludge to get things running because &getGlobal('SERVICE' doesn't
  # return the expected values.
  @params = ();
  push (@params, $service);

  foreach my $param (@params) {
    &B_log("DEBUG","Checking to see if service $service is off.\n");

    if ($serviceType =~ /rc/) {
      my $on = &B_Backtick("$chkconfig --list $param 2>&1");
      if ($on =~ /^$param:\s+unknown/) {
          # This service isn't installed on the system
          return NOT_INSTALLED();
      }
      if ($on =~ /^error reading information on service $param: No such file or directory/) {
          # This service isn't installed on the system
          return NOT_INSTALLED();
      }
      if ($on =~ /^error/) {
          # This probably
          &B_log("DEBUG","chkconfig returned: $param=$on\n");
          return undef;
      }
      $on =~ s/^$param\s+//;            # remove the service name and spaces
      $on =~ s/[0-6]:off\s*//g;         # remove any runlevel:off entries
      $on =~ s/:on\s*//g;               # remove the :on from the runlevels
      # what remains is a list of runlevels in which the service is on,
      # or a null string if it is never turned on
      chomp $on;                        # newline should be gone already (\s)
      &B_log("DEBUG","chkconfig returned: $param=$on\n");

      if ($on =~ /^\d+$/) {
        # service is not off
        ###########################   BREAK out, don't skip question
        return NOTSECURE_CAN_CHANGE();
      }
  }
    elsif ($serviceType =~ /inet/) {
        my $on = &B_Backtick("$chkconfig --list $param 2>&1");
        if ($on =~ /^$param:\s+unknown/) {
            # This service isn't installed on the system
            return NOT_INSTALLED();
        }
        if ($on =~ /^error reading information on service $param: No such file or directory/) {
            # This service isn't installed on the system
            return NOT_INSTALLED();
        }
        if ($on =~ /^error/ ) {
         # Something else is wrong?
         # return undef
         return undef;
     }
      if ($on =~ tr/\n// > 1) {
        $on =~ s/^xinetd.+\n//;
      }
      $on =~ s/^\s*$param:?\s+//;       # remove the service name and spaces
      chomp $on;                        # newline should be gone already (\s)
      &B_log("DEBUG","chkconfig returned: $param=$on\n");

      if ($on =~ /^on$/) {
        # service is not off
        ###########################   BREAK out, don't skip question
        return NOTSECURE_CAN_CHANGE();
      }
    }
    else {
      # perhaps the service is started by inittab
      my $inittabline = &B_Backtick("$grep -E '^[^#].{0,3}:.*:.+:.*$param' $inittab");
      if ($inittabline =~ /.+/) {  # . matches anything except newlines
        # service is not off
        &B_log("DEBUG","Checking inittab; found $inittabline\n");
        ###########################   BREAK out, don't skip question
        return NOTSECURE_CAN_CHANGE();
      }
    }
  }  # foreach my $param


  # boot-time parameters are not set; check processes
  # Note the checkProcsforService returns INCONSISTENT() if a process is found
  # assuming the checks above
  return &checkProcsForService($service);
}

1;


