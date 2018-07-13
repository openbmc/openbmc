package Bastille::API::HPSpecific;

use strict;
use Bastille::API;
use Bastille::API::FileContent;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT_OK = qw(
getIPFLocation
getGlobalSwlist
B_check_system
B_swmodify
B_load_ipf_rules
B_Schedule
B_ch_rc
B_set_value
B_chperm
B_install_jail
B_list_processes
B_list_full_processes
B_deactivate_inetd_service
B_get_rc
B_set_rc
B_chrootHPapache
isSystemTrusted
isTrustedMigrationAvailable
checkServiceOnHPUX
B_get_path
convertToTrusted
isOKtoConvert
convertToShadow
getSupportedSettings
B_get_sec_value
secureIfNoNameService
isUsingRemoteNameService
remoteServiceCheck
remoteNISPlusServiceCheck
B_create_nsswitch_file
B_combine_service_results

%priorBastilleNDD
%newNDD
);
our @EXPORT = @EXPORT_OK;



# "Constants" for use both in testing and in lock-down
our %priorBastilleNDD = (
   "ip_forward_directed_broadcasts" =>["ip",   "0"],
   "ip_forward_src_routed"          =>["ip",   "0"],
   "ip_forwarding"                  =>["ip",   "0"],
   "ip_ire_gw_probe"                =>["ip",   "0"],
   "ip_pmtu_strategy"               =>["ip",   "1"],
   "ip_respond_to_echo_broadcast"   =>["ip",   "0"],
   "ip_send_redirects"              =>["ip",   "0"],
   "ip_send_source_quench"          =>["ip",   "0"],
   "tcp_syn_rcvd_max"               =>["tcp","1000"],
   "tcp_conn_request_max"           =>["tcp","4096"] );

our %newNDD = (
   "ip_forward_directed_broadcasts" =>["ip",    "0"],
   "ip_forward_src_routed"          =>["ip",    "0"],
   "ip_forwarding"                  =>["ip",    "0"],
   "ip_ire_gw_probe"                =>["ip",    "0"],
   "ip_pmtu_strategy"               =>["ip",    "1"],
   "ip_respond_to_echo_broadcast"   =>["ip",    "0"],
   "ip_send_redirects"              =>["ip",    "0"],
   "ip_send_source_quench"          =>["ip",    "0"],
   "tcp_syn_rcvd_max"               =>["tcp","4096"],
   "tcp_conn_request_max"           =>["tcp","4096"],
   "arp_cleanup_interval"           =>["arp","60000"],
   "ip_respond_to_timestamp"        =>["ip",    "0"],
   "ip_respond_to_timestamp_broadcast" => ["ip","0"] );


####################################################################
#
#  This module makes up the HP-UX specific API routines.
#
####################################################################
#
#  Subroutine Listing:
#     &HP_ConfigureForDistro: adds all used file names to global
#                             hashes and generates a global IPD
#                             hash for SD modification lookup.
#
#     &getGlobalSwlist($):    Takes a fully qualified file name
#                             and returns product:filset info
#                             for that file.  returns undef if
#                             the file is not present in the IPD
#
#     &B_check_system:        Runs a series of system queries to
#                             determine if Bastille can be safely
#                             ran on the current system.
#
#     &B_swmodify($):         Takes a file name and runs the
#                             swmodify command on it so that the
#                             IPD is updated after changes
#
#     &B_System($$):          Takes a system command and the system
#                             command that should be used to revert
#                             whatever was done. Returns 1 on
#                             success and 0 on failure
#
#     &B_Backtick($)          Takes a command to run and returns its stdout
#                             to be used in place of the prior prevelent use
#                             of un-error-handled backticks
#
#     &B_load_ipf_rules($):   Loads a set of ipfrules into ipf, storing
#                             current rules for later reversion.
#
#     &B_Schedule($$):        Takes a pattern and a crontab line.
#                             Adds or replaces the crontab line to
#                             the crontab file, depending on if a
#                             line matches the pattern
#
#     &B_ch_rc($$):           Takes a the rc.config.d flag name and
#                             new value as well as the init script
#                             location. This will stop a services
#                             and set the service so that it will
#                             not be restarted.
#
#     &B_set_value($$$):      Takes a param, value, and a filename
#                             and sets the given value in the file.
#                             Uses ch_rc, but could be rewritten using
#                             Bastille API calls to make it work on Linux
#
#     &B_TODO($):             Appends the give string to the TODO.txt
#                             file.
#
#     &B_chperm($$$$):        Takes new perm owner and group of given
#                             file.  TO BE DEPRECATED!!!
#
#     &B_install_jail($$):    Takes the jail name and the jail config
#                             script location for a give jail...
#                             These scripts can be found in the main
#                             directory e.g. jail.bind.hpux
#
#####################################################################

##############################################################################
#
#                     HP-UX Bastille directory structure
#
##############################################################################
#
#  /opt/sec_mgmt/bastille/bin/   -- location of Bastille binaries
#  /opt/sec_mgmt/bastille/lib/   -- location of Bastille modules
#  /opt/sec_mgmt/bastille/doc/   -- location of Bastille doc files
#
#  /etc/opt/sec_mgmt/bastille/   -- location of Bastille config files
#
#  /var/opt/sec_mgmt/bastille/log         -- location of Bastille log files
#  /var/opt/sec_mgmt/bastille/revert        -- directory holding all Bastille-
#                                            created revert scripts
#  /var/opt/sec_mgmt/bastille/revert/backup -- directory holding the original
#                                            files that Bastille modifies,
#                                            with permissions intact
#
##############################################################################

sub getIPFLocation () { # Temporary until we get defined search space support
    my $ipf=&getGlobal('BIN','ipf_new');
    my $ipfstat=&getGlobal('BIN','ipfstat_new');
    if (not(-e $ipf)) { # Detect if the binaries moved
        $ipf = &getGlobal('BIN','ipf');
        $ipfstat=&getGlobal('BIN','ipfstat');
    }
    return ($ipf, $ipfstat);
}

##############################################
# Given a combination of service results, provided
# in an array, this function combines the result into
# a reasonable aggregate result
##############################################

sub B_combine_service_results(@){
    my @results = @_;
    
    #TODO: Consider greater sophistication wrt inconsistent, or not installed.
    
    foreach my $result (@results) {
        if (not(($result ==  SECURE_CAN_CHANGE) or
            ($result ==  SECURE_CANT_CHANGE) or
            ($result == NOT_INSTALLED()))) {
            return NOTSECURE_CAN_CHANGE();
        }
    }
    return SECURE_CANT_CHANGE();
}

####################################################################
# &getGlobalSwlist ($file);
#   This function returns the product and fileset information for
#   a given file or directory if it exists in the IPD otherwise
#   it returns undefined "undef"
#
#   uses $GLOBAL_SWLIST{"$FILE"}
####################################################################
sub getGlobalSwlist($){
    no strict;
    my $file = $_[0];


    if(! %GLOBAL_SWLIST) {
	# Generating swlist database for swmodify changes that will be required
	# The database will be a hash of fully qualified file names that reference
	# the files product name and fileset.  These values are required to use
	# swmodify...

	# Files tagged 'is_volatile' in the IPD are not entered in the swlist database
	# in order to avoid invoking swmodify if the file is changed later.  Attempting to 
	# swmodify 'volatile' files is both unneccessary and complicated since swverify will 
	# not evaluate volatile files anyway, and adding another value to the swlist database
	# would require complex code changes.

	# temp variable to keep swlist command /usr/sbin/swlist
	my $swlist = &getGlobal('BIN',"swlist");

	# listing of each directory and file that was installed by SD on the target machine
	my @fileList = `$swlist -a is_volatile -l file`;

	# listing of each patch and the patches that supersede each.
	# hash which is indexed by patch.fileset on the system
	my %patchSuperseded;

	my @patchList = `${swlist} -l fileset -a superseded_by *.*,c=patch 2>&1`;
	# check to see if any patches are present on the system
	if(($? >> 8) == 0) {

	    # determining patch suppression for swmodify.
	    foreach my $patchState (@patchList) {
		# removing empty lines and commented lines.
		if($patchState !~ /^\s*\#/ && $patchState !~ /^\s*$/) {

		    # removing leading white space
		    $patchState =~ s/^\s+//;
		    my @patches = split /\s+/, $patchState;
		    if($#patches == 0){
			# patch is not superseded
			$patchSuperseded{$patches[0]} = 0;
		    }
		    else {
			# patch is superseded
			$patchSuperseded{$patches[0]} = 1;
		    }
		}
	    }
	}
	else {
	    &B_log("DEBUG","No patches found on the system.\n");
	}

	if($#fileList >= 0){
	    # foreach line of swlist output
	    foreach my $fileEntry ( @fileList ){
		#filter out commented portions
		if( $fileEntry !~ /^\s*\#/ ){
		    chomp $fileEntry;
		    # split the output into three fields: product.fileset, filename, flag_isvolatile
		    my( $productInfo, $file, $is_volatile ) = $fileEntry =~ /^\s*(\S+): (\S+)\t(\S+)/ ;
		    # do not register volatile files
		    next if ($is_volatile =~ /true/);  # skip to next file entry
		    $productInfo =~ s/\s+//;
		    $file =~ s/\s+//;
		    # if the product is a patch
		    if($productInfo =~ /PH(CO|KL|NE|SS)/){
			# if the patch is not superseded by another patch
			if($patchSuperseded{$productInfo} == 0){
			    # add the patch to the list of owner for this file
			    push @{$GLOBAL_SWLIST{"$file"}}, $productInfo;
			}
		    }
		    # not a patch.
		    else {
			# add the product to the list of owners for this file
			push @{$GLOBAL_SWLIST{"$file"}}, $productInfo;
		    }

		}
	    }
	}
	else{
	    # defining GLOBAL_SWLIST in error state.
	    $GLOBAL_SWLIST{"ERROR"} = "ERROR";
	    &B_log("ERROR","Could not execute swlist.  Swmodifys will not be attempted");
	}
    }

    if(exists $GLOBAL_SWLIST{"$file"}){
	return $GLOBAL_SWLIST{"$file"};
    }
    else {
	return undef;
    }
}

###################################################################
#  &B_check_system;
#    This subroutine is called to validate that bastille may be
#    safely run on the current system.  It will check to insure
#    that there is enough file system space, mounts are rw, nfs
#    mounts are not mounted noroot, and swinstall, swremove and
#    swmodify are not running
#
#    uses ErrorLog
#
##################################################################
sub B_check_system {
    # exitFlag is one if a conflict with the successful execution
    # of bastille is found.
    my $exitFlag = 0;

    my $ignoreCheck = &getGlobal("BDIR","config") . "/.no_system_check";
    if( -e $ignoreCheck ) {
	return $exitFlag;
    }

    # first check for swinstall, swmodify, or swremove processes
    my $ps = &getGlobal('BIN',"ps") . " -el";
    my @processTable = `$ps`;
    foreach my $process (@processTable) {
	if($process =~ /swinstall/ ) {
	    &B_log("ERROR","Bastille cannot run while a swinstall is in progress.\n" .
		      "Complete the swinstall operation and then run Bastille.\n\n");
	    $exitFlag = 1;
	}

	if($process =~ /swremove/ ) {
	    &B_log("ERROR","Bastille cannot run while a swremove is in progress.\n" .
		      "Complete the swremove operation and then run Bastille.\n\n");
	    $exitFlag = 1;
	}

	if($process =~ /swmodify/ ) {
	    &B_log("ERROR","Bastille cannot run while a swmodify is in progress.\n" .
		      "Complete the swmodify operation and then run Bastille.\n\n");
	    $exitFlag = 1;
	}

    }

    # check for root read only mounts for /var /etc /stand /
    # Bastille is required to make changes to these file systems.
    my $mount = &getGlobal('BIN',"mount");
    my $rm = &getGlobal('BIN',"rm");
    my $touch = &getGlobal('BIN',"touch");

    my @mnttab = `$mount`;

    if(($? >> 8) != 0) {
	&B_log("WARNING","Unable to use $mount to determine if needed partitions\n" .
		  "are root writable, based on disk mount options.\n" .
		  "Bastille will continue but note that disk\n" .
		  "mount checks were skipped.\n\n");
    }
    else {
	foreach my $record (@mnttab) {
	    my @fields = split /\s+/, $record;
	    if ((defined $fields[0]) && (defined $fields[2]) && (defined $fields[3])) {
		my $mountPoint = $fields[0];
		my $mountType =  $fields[2];
		my $mountOptions = $fields[3];

		# checks for /stand and /var/* removed 
		if($mountPoint =~ /^\/$|^\/etc|^\/var$/) {

		    if($mountOptions =~ /^ro,|,ro,|,ro$/) {
			&B_log("ERROR","$mountPoint is mounted read-only.  Bastille needs to make\n" .
				  "modifications to this file system.  Please remount\n" .
				  "$mountPoint read-write and then run Bastille again.\n\n");
			$exitFlag = 1;
		    }
		    # looking for an nfs mounted file system
		    if($mountType =~/.+:\//){
			my $fileExisted=0;
			if(-e "$mountPoint/.bastille") {
			    $fileExisted=1;
			}

			`$touch $mountPoint/.bastille 1>/dev/null 2>&1`;

			if( (! -e "$mountPoint/.bastille") || (($? >> 8) != 0) ) {
			    &B_log("ERROR","$mountPoint is an nfs mounted file system that does\n" .
				   "not allow root to write to.  Bastille needs to make\n" .
				   "modifications to this file system.  Please remount\n" .
				   "$mountPoint giving root access and then run Bastille\n" .
				   "again.\n\n");

			    $exitFlag = 1;
			}
			# if the file did not exist befor the touch then remove the generated file
			if(! $fileExisted) {
			    `$rm -f $mountPoint/.bastille 1>/dev/null 2>&1`;
			}
		    }
		}
	    }
	    else {
		&B_log("WARNING","Unable to use $mount to determine if needed partitions\n" .
			  "are root writable, based on disk mount options.\n" .
			  "Bastille will continue but note that disk\n" .
			  "mount checks were skipped.\n\n");
	    }
	}

    }

    # checks for enough disk space in directories that Bastille writes to.
    my $bdf = &getGlobal('BIN',"bdf");
    #directories that Bastille writes to => required space in kilobytes.
    my %bastilleDirs = ( "/etc/opt/sec_mgmt/bastille" => "4", "/var/opt/sec_mgmt/bastille"=> "1000");
    for my $directory (sort keys %bastilleDirs) {
	my @diskUsage = `$bdf $directory`;

	if(($? >> 8) != 0) {
	    &B_log("WARNING","Unable to use $bdf to determine disk usage for\n" .
		   "$directory\n" .
		   "Bastille will continue but note that disk\n" .
		   "usage checks were skipped.\n\n");

	}
	else {
	    # removing bdf header line from usage information.
	    shift @diskUsage;
	    my $usageString= "";

	    foreach my $usageRecord (@diskUsage) {
		chomp $usageRecord;
	        $usageString .= $usageRecord;
	    }

	    $usageString =~ s/^\s+//;

	    my @fields = split /\s+/, $usageString;
	    if($#fields != 5) {
		&B_log("WARNING","Unable to use $bdf to determine disk usage for\n" .
		       "$directory\n" .
		       "Bastille will continue but note that disk\n" .
		       "usage checks were skipped.\n\n");
	    }
	    else {

		my $mountPoint = $fields[5];
		my $diskAvail = $fields[3];

		if($diskAvail <= $bastilleDirs{"$directory"}) {
		    &B_log("ERROR","$mountPoint does not contain enough available space\n" .
			      "for Bastille to run properly.  $directory needs\n" .
			      "at least $bastilleDirs{$directory} kilobytes of space.\n" .
			      "Please clear at least that amount of space from\n" .
			      "$mountPoint and run Bastille again.\n" .
			      "Current Free Space available = ${diskAvail} k\n\n");
 		    $exitFlag = 1;
		}
	    }
	}
    }

    # check to make sure that we are in at least run level 2 before we attempt to run
    my $who = &getGlobal('BIN', "who") . " -r";
    my $levelInfo = `$who`;
    if(($? >> 8) != 0 ) {
	&B_log("WARNING","Unable to use \"$who\" to determine system run.\n" .
		  "level Bastille will continue but note that the run\n" .
		  "level check was skipped.\n\n");
    }
    else {
	chomp $levelInfo;
	my @runlevel = split /\s+/, $levelInfo;
	if ((! defined $runlevel[3]) or ($runlevel[3] < 2)) {
	    &B_log("WARNING","Bastille requires a run-level of 2 or more to run properly.\n" .
		      "Please move your system to a higher run level and then\n" .
		      "run 'bastille -b'.\n\n");
	    if(defined $runlevel[3]) {
		&B_log("ERROR","Current run-level is '$runlevel[3]'.\n\n");
		$exitFlag=1;
	    }
	    else {
		&B_log("WARNING","Unable to use \"$who\" to determine system run.\n" .
			  "level Bastille will continue but note that the run\n" .
			  "level check was skipped.\n\n");
	    }
	}
	else {
	    &B_log("DEBUG","System run-level is $runlevel[3]\n");
	}
    }

    if($exitFlag) {
	exit(1);
    }

}

###################################################################
#  &B_swmodify($file);
#    This subroutine is called after a file is modified.  It will
#    redefine the file in the IPD with it's new properties.  If
#    the file is not in the IPD it does nothing.
#
#    uses B_System to make the swmodifications.
##################################################################
sub B_swmodify($){
    my $file = $_[0];
    if(defined &getGlobalSwlist($file)){
	my $swmodify = &getGlobal('BIN',"swmodify");
	my @productsInfo = @{&getGlobalSwlist($file)};
	# running swmodify on files that were altered by this function but
	# were created and maintained by SD
	foreach my $productInfo (@productsInfo) {
	    &B_System("$swmodify -x files='$file' $productInfo",
		      "$swmodify -x files='$file' $productInfo");
	}
    }
}

####################################################################
#  &B_load_ipf_rules($ipfruleset);
#    This function enables an ipfruleset.  It's a little more
#    specific than most API functions, but necessary because
#    ipf doesn't return correct exit codes (syntax error results
#    in a 0 exit code)
#
#   uses ActionLog and ErrorLog to log
#   calls crontab directly (to list and to read in new jobs)
###################################################################
sub B_load_ipf_rules ($) {
   my $ipfruleset=$_[0];

   &B_log("DEBUG","# sub B_load_ipf_rules");

   # TODO: grab ipf.conf dynamically from the rc.config.d files
   my $ipfconf = &getGlobal('FILE','ipf.conf');

   # file system changes - these are straightforward, and the API
   # will take care of the revert
   &B_create_file($ipfconf);
   &B_blank_file($ipfconf, 'a$b');
   &B_append_line($ipfconf, 'a$b', $ipfruleset);

   # runtime changes

   # define binaries
   my $grep = &getGlobal('BIN', 'grep');
   my ($ipf, $ipfstat) = &getIPFLocation;
   # create backup rules
   # This will exit with a non-zero exit code because of the grep
   my @oldrules = `$ipfstat -io 2>&1 | $grep -v empty`;

   my @errors=`$ipf -I -Fa -f $ipfconf 2>&1`;

   if(($? >> 8) == 0) {

      &B_set_rc("IPF_START","1");
      &B_set_rc("IPF_CONF","$ipfconf");

      # swap the rules in
      &B_System("$ipf -s","$ipf -s");

      # now create a "here" document with the previous version of
      # the rules and put it into the revert-actions script
      &B_revert_log("$ipf -I -Fa -f - <<EOF\n@{oldrules}EOF");

      if (@errors) {
        &B_log("ERROR","ipfilter produced the following errors when\n" .
                  "        loading $ipfconf.  You probably had an invalid\n" .
                  "        rule in ". &getGlobal('FILE','customipfrules') ."\n".
                  "@errors\n");
      }

   } else {
     &B_log("ERROR","Unable to run $ipf\n");
   }

}



####################################################################
#  &B_Schedule($pattern,$cronjob);
#    This function schedules a cronjob.  If $pattern exists in the
#    crontab file, that job will be replaced.  Otherwise, the job
#    will be appended.
#
#   uses ActionLog and ErrorLog to log
#   calls crontab directly (to list and to read in new jobs)
###################################################################
sub B_Schedule ($$) {
   my ($pattern,$cronjob)=@_;
   $cronjob .= "\n";

   &B_log("DEBUG","# sub B_Schedule");
   my $crontab = &getGlobal('BIN','crontab');

   my @oldjobs = `$crontab -l 2>/dev/null`;
   my @newjobs;
   my $patternfound=0;

   foreach my $oldjob (@oldjobs) {
       if (($oldjob =~ m/$pattern/ ) and (not($patternfound))) {
	   push @newjobs, $cronjob;
	   $patternfound=1;
	   &B_log("ACTION","changing existing cron job which matches $pattern with\n" .
		  "$cronjob");
       } elsif ($oldjob !~ m/$pattern/ ) {
       	&B_log("ACTION","keeping existing cron job $oldjob");
      	push @newjobs, $oldjob;
       } #implied: else if pattern matches, but we've
          #already replaced one, then toss the others.
   }

   unless ($patternfound) {
     &B_log("ACTION","adding cron job\n$cronjob\n");
     push @newjobs, $cronjob;
   }

   if(open(CRONTAB, "|$crontab - 2> /dev/null")) {
     print CRONTAB @newjobs;

     # now create a "here" document with the previous version of
     # the crontab file and put it into the revert-actions script
     &B_revert_log("$crontab <<EOF\n" . "@oldjobs" . "EOF");
     close CRONTAB;
   }

   # Now check to make sure it happened, since cron will exit happily
   # (retval 0) with no changes if there are any syntax errors
   my @editedjobs = `$crontab -l 2>/dev/null`;

   if (@editedjobs ne @newjobs) {
     &B_log("ERROR","failed to add cron job:\n$cronjob\n" .
               "         You probably had an invalid crontab file to start with.");
   }

}


#This function turns off a service, given a service name defined in HP-UX.service

sub B_ch_rc($) {

    my ($service_name)=@_;

    if (&GetDistro != "^HP-UX") {
       &B_log("ERROR","Tried to call ch_rc $service_name on a non-HP-UX\n".
                 "         system!  Internal Bastille error.");
       return undef;
    }
    my $configfile="";
    my $command = &getGlobal('BIN', 'ch_rc');
    
    my $startup_script=&getGlobal('DIR','initd') . "/". $service_name;
    my @rc_parameters= @{ &getGlobal('SERVICE',$service_name) };
    my @rcFiles=@{ &getGlobal('RCCONFIG',$service_name) };
    my $rcFile='';
    if (@rcFiles == 1){
        $rcFile=$rcFiles[0];
    } else {
        &B_log("FATAL","Multiple RC Files not yet supported... internal error.");
    }
    
    # if the service-related process is not run, and the control variable is stilll 1
    # there is a inconsistency.  in this case we only need to change the control variable
    my @psnames=@{ &getGlobal('PROCESS',$service_name)};
    my @processes;
    foreach my $psname (@psnames) {
        $psname .= '\b'; # avoid embedded match; anchor search pattern to trailing word boundry
        my @procList = &isProcessRunning($psname);
        if(@procList >= 0){
          splice @processes,$#processes+1,0,@procList;
        }
    }
#Actually set the rc variable
  foreach my $rcVariable (@rc_parameters){
    my $orig_value = &B_get_rc($rcVariable);
    if ($orig_value eq "" ) { #If variable not set, used the defined file
        $configfile=&getGlobal("DIR","rc.config.d") . "/" . $rcFile;
        if (not( -f $configfile )) {
            &B_create_file($configfile);
        }
    }
    &B_log("DEBUG","In B_ch_rc (no procs), setting $rcVariable to 0 in $configfile" .
           ", with an original value of $orig_value with rcfile: $rcFile");
    if ( ! @processes) { # IF there are no processes we don't neet to perform a "stop"
            &B_set_rc($rcVariable, "0", $configfile);
    } else {
        if ( $orig_value !~ "1" ) { #If param is not already 1, the "stop" script won't work
            &B_set_rc($rcVariable, "1",$configfile);
        }
        &B_System ($startup_script  . " stop", #stop service, then restart if the user runs bastille -r
                   $startup_script . " start");
        # set parameter, so that service will stay off after reboots
        &B_set_rc($rcVariable, "0", $configfile);
    }
  }
}


# This routine sets a value in a given file
sub B_set_value($$$) {
    my ($param, $value, $file)=@_;

    &B_log("DEBUG","B_set_value: $param, $value, $file");
    if (! -e $file ) {
	&B_create_file("$file");
    }

    # If a value is already set to something other than $value then reset it.
    #Note that though this tests for "$value ="the whole line gets replaced, so
    #any pre-existing values are also replaced.
    &B_replace_line($file,"^$param\\s*=\\s*","$param=$value\n");
    # If the value is not already set to something then set it.
    &B_append_line($file,"^$param\\s*=\\s*$value","$param=$value\n");

}


##################################################################################
# &B_chperm($owner,$group,$mode,$filename(s))
#   This function changes ownership and mode of a list of files. Takes four
#   arguments first the owner next the group and third the new mode in oct and
#   last a list of files that the permissions changes should take affect on.
#
#   uses: &swmodify and &B_revert_log
##################################################################################
sub B_chperm($$$$) {
    my ($newown, $newgrp, $newmode, $file_expr) = @_;
    my @files = glob($file_expr);

    my $return = 1;

    foreach my $file (@files){
	my @filestat = stat $file;
	my $oldmode = (($filestat[2]/512) % 8) .
	    (($filestat[2]/64) % 8) .
		(($filestat[2]/8) % 8) .
		    (($filestat[2]) % 8);

	if((chown $newown, $newgrp, $file) != 1 ){
	    &B_log("ERROR","Could not change ownership of $file to $newown:$newgrp\n");
	    $return = 0;
	}
	else{
	    &B_log("ACTION","Changed ownership of $file to $newown:$newgrp\n");
	    # swmodifying file if possible...
	    &B_swmodify($file);
	    &B_revert_log(&getGlobal('BIN',"chown") . " $filestat[4]:$filestat[5] $file\n");
	}

        my $newmode_formatted=sprintf "%5lo",$newmode;

	if((chmod $newmode, $file) != 1){
	    &B_log("ERROR","Could not change mode of $file to $newmode_formatted\n");
	    $return = 0;
	}
	else{
	    &B_log("ACTION","Changed mode of $file to $newmode_formatted\n");
	    &B_revert_log(&getGlobal('BIN',"chmod") . " $oldmode $file\n");
	}


    }
    return $return;
}

############################################################################
# &B_install_jail($jailname, $jailconfigfile);
# This function takes two arguments ( jail_name, jail_config )
# It's purpose is to take read in config files that define a
# chroot jail and then generate it bases on that specification
############################################################################
sub B_install_jail($$) {

    my $jailName = $_[0];  # Name of the jail e.g bind
    my $jailConfig = $_[1]; # Name of the jails configuration file
    # create the root directory of the jail if it does not exist
    &B_create_dir( &getGlobal('BDIR','jail'));
    &B_chperm(0,0,0555,&getGlobal('BDIR','jail'));

    # create the Jail dir if it does not exist
    &B_create_dir( &getGlobal('BDIR','jail') . "/" . $jailName);
    &B_chperm(0,0,0555,&getGlobal('BDIR','jail') . "/". $jailName);


    my $jailPath = &getGlobal('BDIR','jail') . "/" . $jailName;
    my @lines; # used to store no commented no empty config file lines
    # open configuration file for desired jail and parse in commands
    if(open(JAILCONFIG,"< $jailConfig")) {
	while(my $line=<JAILCONFIG>){
	    if($line !~ /^\s*\#|^\s*$/){
		chomp $line;
		push(@lines,$line);
	    }
	}
        close JAILCONFIG;
    }
    else{
	&B_log("ERROR","Open Failed on filename: $jailConfig\n");
	return 0;
    }
    # read through commands and execute
    foreach my $line (@lines){
        &B_log("ACTION","Install jail: $line\n");
	my @confCmd = split /\s+/,$line;
	if($confCmd[0] =~ /dir/){ # if the command say to add a directory
	    if($#confCmd == 4) { # checking dir Cmd form
		if(! (-d  $jailPath . "/" . $confCmd[1])){
		    #add a directory and change its permissions according
                    #to the conf file
		    &B_create_dir( $jailPath . "/" . $confCmd[1]);
                    &B_chperm((getpwnam($confCmd[3]))[2],
                              (getgrnam($confCmd[4]))[2],
                               oct($confCmd[2]),
                               $jailPath . "/" . $confCmd[1]);
		}
	    }
	    else {
		&B_log("ERROR","Badly Formed Configuration Line:\n$line\n\n");
	    }
	}
	elsif($confCmd[0] =~ /file/) {
	    if($#confCmd == 5) { # checking file cmd form
		if(&B_cp($confCmd[1],$jailPath . "/" . $confCmd[2])){
		    # for copy command cp file and change perms
		    &B_chperm($confCmd[4],$confCmd[5],oct($confCmd[3]),$jailPath . "/" . $confCmd[2]);
		}
		else {
		    &B_log("ERROR","Could not complete copy on specified files:\n" .
			   "$line\n");
		}
	    }
	    else {
		&B_log("ERROR","Badly Formed Configuration Line:\n" .
		       "$line\n\n");
	    }
	}
	elsif($confCmd[0] =~ /slink/) {
	    if($#confCmd == 2) { # checking file cmd form
		if(!(-e $jailPath . "/" . $confCmd[2])){
		    #for symlink command create the symlink
		    &B_symlink($jailPath . "/" . $confCmd[1], $confCmd[2]);
		}
	    }
	    else {
		&B_log("ERROR","Badly Formed Configuration Line:\n" .
		       "$line\n\n");
	    }
	}
	else {
	    &B_log("ERROR","Unrecognized Configuration Line:\n" .
		   "$line\n\n");
	}
    }
    return 1;
}



###########################################################################
#  &B_list_processes($service)                                            #
#                                                                         #
#  This subroutine uses the GLOBAL_PROCESS hash to determine if a         #
#  service's corresponding processes are running on the system.           #
#  If any of the processes are found to be running then the process       #
#  name(s) is/are returned by this subroutine in the form of an list      #
#  If none of the processes that correspond to the service are running    #
#  then an empty list is returned.                                        #
###########################################################################
sub B_list_processes($) {

    # service name
    my $service = $_[0];
    # list of processes related to the service
    my @processes=@{ &getGlobal('PROCESS',$service)};

    # current systems process information
    my $ps = &getGlobal('BIN',"ps");
    my $psTable = `$ps -elf`;

    # the list to be returned from the function
    my @running_processes;

    # for every process associated with the service
    foreach my $process (@processes) {
	# if the process is in the process table then
	if($psTable =~ m/$process/) {
	    # add the process to the list, which will be returned
	    push @running_processes, $process;
	}

    }

    # return the list of running processes
    return @running_processes;

}

#############################################################################
#  &B_list_full_processes($service)                                         #
#                                                                           #
#  This subroutine simply grep through the process table for those matching #
#  the input argument  TODO: Allow B_list process to levereage this code    #
#  ... Not done this cycle to avoid release risk (late in cycle)            #
#############################################################################
sub B_list_full_processes($) {

    # service name
    my $procName = $_[0];
    my $ps = &getGlobal('BIN',"ps");
    my @psTable = split(/\n/,`$ps -elf`);

    # for every process associated with the service
    my @runningProcessLines = grep(/$procName/ , @psTable);
    # return the list of running processes
    return @runningProcessLines;
}

################################################################################
#  &B_deactivate_inetd_service($service);                                      #
#                                                                              #
#  This subroutine will disable all inetd services associated with the input   #
#  service name.  Service name must be a reference to the following hashes     #
#  GLOBAL_SERVICE GLOBAL_SERVTYPE and GLOBAL_PROCESSES.  If processes are left #
#  running it will note these services in the TODO list as well as instruct the#
#  user in how they remaining processes can be disabled.                       #
################################################################################
sub B_deactivate_inetd_service($) {
    my $service = $_[0];
    my $servtype = &getGlobal('SERVTYPE',"$service");
    my $inetd_conf = &getGlobal('FILE',"inetd.conf");

    # check the service type to ensure that it can be configured by this subroutine.
    if($servtype ne 'inet') {
	&B_log("ACTION","The service \"$service\" is not an inet service so it cannot be\n" .
		   "configured by this subroutine\n");
	return 0;
    }

    # check for the inetd configuration files existence so it may be configured by
    # this subroutine.
    if(! -e $inetd_conf ) {
	&B_log("ACTION","The file \"$inetd_conf\" cannot be located.\n" .
		   "Unable to configure inetd\n");
	return 0;
    }

    # list of service identifiers present in inetd.conf file.
    my @inetd_entries = @{ &getGlobal('SERVICE',"$service") };

    foreach my $inetd_entry (@inetd_entries) {
	&B_hash_comment_line($inetd_conf, "^\\s*$inetd_entry");
    }

    # list of processes associated with this service which are still running
    # on the system
    my @running_processes = &B_list_processes($service);

    if($#running_processes >= 0) {
        my $todoString = "\n" .
	                 "---------------------------------------\n" .
	                 "Deactivating Inetd Service: $service\n" .
			 "---------------------------------------\n" .
			 "The following process(es) are associated with the inetd service \"$service\".\n" .
			 "They are most likely associated with a session which was initiated prior to\n" .
			 "running Bastille.  To disable a process see \"kill(1)\" man pages or reboot\n" .
			 "the system\n" .
			 "Active Processes:\n" .
			 "###################################\n";
	foreach my $running_process (@running_processes) {
	    $todoString .= "\t$running_process\n";
	}
	$todoString .= 	 "###################################\n";

	&B_TODO($todoString);
    }

}


################################################################################
# B_get_rc($key);                                                              #
#                                                                              #
#  This subroutine will use the ch_rc binary to get rc.config.d variables      #
#  values properly escaped and quoted.                                         #
################################################################################
sub B_get_rc($) {
    
    my $key=$_[0];
    my $ch_rc = &getGlobal('BIN',"ch_rc");

    # get the current value of the given parameter.
    my $currentValue=`$ch_rc -l -p $key`;
    chomp $currentValue;
    
    if(($? >> 8) == 0 ) {
        # escape all meta characters.
	# $currentValue =~ s/([\"\`\$\\])/\\$1/g; 
        # $currentValue = '"' . $currentValue . '"';
    }
    else {
	return undef;
    }

    return $currentValue;
}



################################################################################
# B_set_rc($key,$value);                                                       #
#                                                                              #
#  This subroutine will use the ch_rc binary to set rc.config.d variables.  As #
#  well as setting the variable this subroutine will set revert strings.       #
#                                                                              #
################################################################################
sub B_set_rc($$;$) {

    my ($key,$value,$configfile)=@_;
    my $ch_rc = &getGlobal('BIN',"ch_rc");

    # get the current value of the given parameter.
    my $currentValue=&B_get_rc($key);
    if(defined $currentValue ) {
        if ($currentValue =~ /^\"(.*)\"$/ ) {
            $currentValue = '"\"' . $1 . '\""';
        }
        if ($value =~ /^\"(.*)\"$/ ) {
            $value = '"\"' . $1 . '\""';
        }
	if ( &B_System("$ch_rc -a -p $key=$value $configfile",
		       "$ch_rc -a -p $key=$currentValue $configfile") ) {
	    #ch_rc success
	    return 1;
	}
	else {
	    #ch_rc failure.
	    return 0;
	}
    }
    else {
	&B_log("ERROR","ch_rc was unable to lookup $key\n");
	return 0;
    }

}


################################################################################
#  &ChrootHPApache($chrootScript,$httpd_conf,$httpd_bin,
#                  $apachectl,$apacheJailDir,$serverString);
#
#     This subroutine given an chroot script, supplied by the vendor, a
#     httpd.conf file, the binary location of httpd, the control script,
#     the jail directory, and the servers identification string, descriptive
#     string for TODO etc.  It makes modifications to httpd.conf so that when
#     Apache starts it will chroot itself into the jail that the above
#     mentions script creates.
#
#     uses B_replace_line B_create_dir B_System B_TODO
#
###############################################################################
sub B_chrootHPapache($$$$$$) {

    my ($chrootScript,$httpd_conf,$httpd_bin,$apachectl,$apacheJailDir,$serverString)= @_;

    my $exportpath = "export PATH=/usr/bin;";
    my $ps = &getGlobal('BIN',"ps");
    my $isRunning = 0;
    my $todo_header = 0;

    # checking for a 2.0 version of the apache chroot script.
    if(-e $chrootScript ) {

	if(open HTTPD, $httpd_conf) {
	    while (my $line = <HTTPD>){
		if($line =~ /^\s*Chroot/) {
		    &B_log("DEBUG","Apache is already running in a chroot as specified by the following line:\n$line\n" .
			   "which appears in the httpd.conf file.  No Apache Chroot action was taken.\n");
		    return;
		}
	    }
	    close(HTTPD);
	}

	if(`$ps -ef` =~ $httpd_bin ) {
	    $isRunning=1;
	    &B_System("$exportpath " . $apachectl . " stop","$exportpath " . $apachectl . " start");
	}
	&B_replace_line($httpd_conf, '^\s*#\s*Chroot' ,
			"Chroot " . $apacheJailDir);
	if(-d &getGlobal('BDIR',"jail")){
	    &B_log("DEBUG","Jail directory already exists. No action taken.\n");
	}
	else{
	    &B_log("ACTION","Jail directory was created.\n");
	    &B_create_dir( &getGlobal('BDIR','jail'));
	}

	if(-d $apacheJailDir){
	    &B_log("DEBUG","$serverString jail already exists. No action taken.\n");
	}
	else{
	    &B_System(&getGlobal('BIN',"umask") . " 022; $exportpath " . $chrootScript,
		      &getGlobal('BIN',"echo") . " \"Your $serverString is now running outside of it's\\n" .
		      "chroot jail.  You must manually migrate your web applications\\n" .
		      "back to your Apache server's httpd.conf defined location(s).\\n".
		      "After you have completed this, feel free to remove the jail directories\\n" .
		      "from your machine.  Your apache jail directory is located in\\n" .
		      &getGlobal('BDIR',"jail") . "\\n\" >> " . &getGlobal('BFILE',"TOREVERT"));

	}
	if($isRunning){
	    &B_System("$exportpath " . $apachectl . " start","$exportpath " . $apachectl . " stop");
	    &B_log("ACTION","$serverString is now running in an chroot jail.\n");
	}

	&B_log("ACTION","The jail is located in " . $apacheJailDir . "\n");

	if ($todo_header !=1){
	    &B_TODO("\n---------------------------------\nApache Chroot:\n" .
		    "---------------------------------\n");
	}
	&B_TODO("$serverString Chroot Jail:\n" .
		"httpd.conf contains the Apache dependencies.  You should\n" .
		"review this file to ensure that the dependencies made it\n" .
		"into the jail.  Otherwise, you run a risk of your Apache server\n" .
		"not having access to all its modules and functionality.\n");


    }

}


sub isSystemTrusted {
        my $getprdef = &getGlobal('BIN',"getprdef");
        my $definition = &B_Backtick("$getprdef -t 2>&1");
        if($definition =~ "System is not trusted.") {
            return 0;
        } else {
            return 1;
        }
}


sub isTrustedMigrationAvailable {
    my $distroVersion='';

    if (&GetDistro =~ '^HP-UX11.(\d*)') {
	$distroVersion=$1;
	if ($distroVersion < 23) { # Not available before 11.23
	    return 0; #FALSE
	} elsif ($distroVersion >= 31) { #Bundled with 11.31 and after
	    &B_log('DEBUG','isTrustedMigrationAvailable: HP-UX 11.31 always has trusted mode extensions');
	    return 1;
	} elsif ($distroVersion == 23) { # Optional on 11.23 if filesets installed
	    if ( -x &getGlobal('BIN',"userdbget") ) {
		&B_log('DEBUG','isTrustedMigrationAvailable: Trusted Extensions Installed');
		return 1;
	    } else {
		&B_log('DEBUG','isTrustedMigrationAvailable: Trusted Extensions Not Installed');
		return 0; #FALSE
	    }
	} else {
	    &B_log('DEBUG','isTrustedMigrationAvailable: ' . &GetDistro .
		   ' not currently supported for trusted extentions.');
	    return 0; #FALSE
	}
    } else {
	&B_log('WARNING','isTrustedMigrationAvailable: HP-UX routine called on Linux system');
	return 0; #FALSE
    }
}



###########################################################################
# &checkServiceOnHPUX($service);
#
# Checks if the given service is running on an HP/UX system.  This is
# called by B_is_Service_Off(), which is the function that Bastille
# modules should call.
#
# Return values:
# NOTSECURE_CAN_CHANGE() if the service is on
# SECURE_CANT_CHANGE() if the service is off
# INCONSISTENT() if the state of the service cannot be determined
# NOT_INSTALLED() if the s/w isn't insalled
#
###########################################################################
sub checkServiceOnHPUX($) {
  my $service=$_[0];

  # get the list of parameters which could be used to initiate the service
  # (could be in /etc/rc.config.d, /etc/inetd.conf, or /etc/inittab, so we
  # check all of them)
  my @params= @{ &getGlobal('SERVICE',$service) };
  my $grep =&getGlobal('BIN', 'grep');
  my $inetd=&getGlobal('FILE', 'inetd.conf');
  my $inittab=&getGlobal('FILE', 'inittab');
  my $retVals;
  my $startup=&getGlobal('DIR','initd') ;
  my @inet_bins= @{ &getGlobal('PROCESS',$service) };
  
  my $entry_found = 0;

  &B_log("DEBUG","CheckHPUXservice: $service");
  my $full_initd_path = $startup . "/" . $service;
  if ($GLOBAL_SERVTYPE{$service} eq "rc") { # look for the init script in /sbin/init.d
    if (not(-e $full_initd_path )) {
        return NOT_INSTALLED();
    }
  } else { #inet-based service, so look for inetd.conf entries.
    &B_log("DEBUG","Checking inet service $service");
    my @inet_entries= @{ &getGlobal('SERVICE',$service) };
    foreach my $service (@inet_entries) {
        &B_log('DEBUG',"Checking for inetd.conf entry of $service in checkService on HPUX");
        my $service_regex = '^[#\s]*' . $service . '\s+';
        if ( &B_match_line($inetd, $service_regex) ) { # inet entry search
            &B_log('DEBUG',"$service present, entry exists");
            $entry_found = 1 ;
        }
    }
    if ($entry_found == 0 ) {
       return NOT_INSTALLED();
    }
  }

 foreach my $param (@params) {
    &B_log("DEBUG","Checking to see if service $service is off.\n");
    if (&getGlobal('SERVTYPE', $service) =~ /rc/) {
      my $ch_rc=&getGlobal('BIN', 'ch_rc');
      my $on=&B_Backtick("$ch_rc -l -p $param");

      $on =~ s/\s*\#.*$//; # remove end-of-line comments
      $on =~ s/^\s*\"(.+)\"\s*$/$1/; # remove surrounding double quotes
      $on =~ s/^\s*\'(.+)\'\s*$/$1/; # remove surrounding single quotes
      $on =~ s/^\s*\"(.+)\"\s*$/$1/; # just in case someone did '"blah blah"'

      chomp $on;
      &B_log("DEBUG","ch_rc returned: $param=$on in checkServiceOnHPUX");

      if ($on =~ /^\d+$/ && $on != 0) {
        # service is on
        &B_log("DEBUG","CheckService found $param service is set to \'on\' in scripts.");
        return NOTSECURE_CAN_CHANGE();
      }
      elsif($on =~ /^\s*$/) {
        # if the value returned is an empty string return
        # INCONSISTENT(), since we don't know what the hard-coded default is.
        return INCONSISTENT();
      }
    } else {
      # those files which rely on comments to determine what gets
      # turned on, such as inetd.conf and inittab
      my $inettabs=&B_Backtick("$grep -e '^[[:space:]]*$param' $inetd $inittab");
      if ($inettabs =~ /.+/) {  # . matches anything except newlines
        # service is not off
        &B_log("DEBUG","Checking inetd.conf and inittab; found $inettabs");
        ###########################   BREAK out, don't skip question
        return NOTSECURE_CAN_CHANGE();
      }
    }
  } # foreach $param

  # boot-time parameters are not set; check processes
  # checkprocs for services returns INCONSISTENT() if a service is found
  # since a found-service is inconsistent with the above checks.
  B_log("DEBUG","Boot-Parameters not set, checking processes.");
  if (&runlevel < 2) { # Below runlevel 2, it is unlikely that
                      #services will be running, so just check "on-disk" state
    &B_log("NOTE","Running during boot sequence, so skipping process checks");
    return SECURE_CANT_CHANGE();
  } else {
    return &checkProcsForService($service);
  }
}

sub runlevel {
    my $who = &getGlobal("BIN", "who");
    my $runlevel = &B_Backtick("$who -r");
    if ($runlevel =~ s/.* run-level (\S).*/$1/) {
        &B_log("DEBUG","Runlevel is: $runlevel");
        return $runlevel;
    } else {
        &B_log("WARNING","Can not determine runlevel, assuming runlevel 3");
        &B_log("DEBUG","Runlevel command output: $runlevel");
        return "3"; #safer since the who command didn't work, we'll assume
                # runlevel 3 since that provides more checks.
    }
}

#
# given a profile file, it will return a PATH array set by the file.
#
sub B_get_path($) {
    my $file = $_[0];
    my $sh = &getGlobal("BIN", "sh");
    # use (``)[0] is becuase, signal 0 maybe trapped which will produce some stdout
    my $path = (`$sh -c '. $file 1>/dev/null 2>&1 < /dev/null ;  echo \$PATH'`)[0];
    my @path_arr = split(":", $path);
    my %tmp_path;
    my %path;
    for my $tmpdir (@path_arr) {
        chomp $tmpdir;
        if ($tmpdir ne ""  && ! $tmp_path{$tmpdir}) {
            $tmp_path{$tmpdir}++;
        }
    }
    return keys %tmp_path;
}

# Convert to trusted mode if it's not already
sub convertToTrusted {
   &B_log("DEBUG","# sub convertToTrusted \n");
   if( ! &isSystemTrusted) {

      my ($ok, $message) = &isOKtoConvert;

      my $ts_header="\n---------------------------------\nTrusted Systems:\n" .
                    "---------------------------------\n";

      if ($ok) {
	# actually do the conversion
        if(&B_System(&getGlobal('BIN','tsconvert'), &getGlobal('BIN','tsconvert') . " -r")){
	  # adjust change times for user passwords to keep them valid
	  # default is to expire them when converting to a trusted system,
	  # which can be problematic, especially since some older versions of
	  # SecureShell do not allow the user to change the password
	  &B_System(&getGlobal('BIN','modprpw') . " -V", "");

	  my $getprdef = &getGlobal('BIN','getprdef');
	  my $oldsettings = &B_Backtick("$getprdef -m lftm,exptm,mintm,expwarn,umaxlntr");
	  $oldsettings =~ s/ //g;

	  # remove password lifetime and increasing login tries so they
	  # don't lock themselves out of the system entirely.
	  # set default expiration time and the like.
	  my $newsettings="lftm=0,exptm=0,mintm=0,expwarn=0,umaxlntr=10";

	  &B_System(&getGlobal('BIN','modprdef') . " -m $newsettings",
		    &getGlobal('BIN','modprdef') . " -m $oldsettings");

          &B_TODO($ts_header .
                  "Your system has been converted to a trusted system.\n" .
                  "You should review the security settings available on a trusted system.\n".
                  "$message");

          # to get rid of "Cron: Your job did not contain a valid audit ID."
          # error, we re-read the crontab file after converting to trusted mode
          # Nothing is necessary in "revert" since we won't be in trusted mode
          # at that time.
          # crontab's errors can be spurious, and this will report an 'error'
          # of the crontab file is missing, so we send stderr to the bit bucket
          my $crontab = &getGlobal('BIN',"crontab");
	  &B_System("$crontab -l 2>/dev/null | $crontab","");
        }

      } else {
          &B_TODO($ts_header . $message);
          return 0; # not ok to convert, so we didn't
      }
   }
   else {
      &B_log("DEBUG","System is already in trusted mode, no action taken.\n");
      return 1;
   }

   # just to make sure
   if( &isSystemTrusted ) {
      return 1;
   } else {
      &B_log("ERROR","Trusted system conversion was unsuccessful for an unknown reason.\n" .
                "         You may try using SAM/SMH to do the conversion instead of Bastille.\n");
      return 0;
   }
}

# isOKtoConvert - check for conflicts between current system state and trusted
# mode
#
# Return values
# 0 - conflict found, see message for details
# 1 - no conflicts, see message for further instructions
#
sub isOKtoConvert {
    &B_log("DEBUG","# sub isOKtoConvert \n");
    # initialize text for TODO instructions
    my $specialinstructions="  - convert to trusted mode\n";

    # These are somewhat out-of-place, but only affect the text of the message.
    # Each of these messages is repeated in a separate TODO item in the
    # appropriate subroutine.
    if (&getGlobalConfig("AccountSecurity","single_user_password") eq "Y") {
	if (&GetDistro =~ "^HP-UX11.(.*)" and $1<23 ) {
	    $specialinstructions .= "  - set a single user password\n";
	}
    }

    if (&getGlobalConfig("AccountSecurity","passwordpolicies") eq "Y") {
	    $specialinstructions .= "  - set trusted mode password policies\n";
    }

    if (&getGlobalConfig("AccountSecurity", "PASSWORD_HISTORY_DEPTHyn") eq "Y") {
       $specialinstructions .= "  - set a password history depth\n";
    }

    if (&getGlobalConfig("AccountSecurity","system_auditing") eq "Y") {
       $specialinstructions .= "  - enable auditing\n";
    }

    my $saminstructions=
	   "The security settings can be modified by running SAM as follows:\n" .
	   "# sam\n" .
	   "Next, go to the \"Auditing and Security Area\" and review\n" .
	   "each sub-section.  Make sure that you review all of your\n" .
	   "settings, as some policies may seem restrictive.\n\n" .
           "On systems using the System Management Homepage, you can\n".
           "change your settings via the Tools:Security Attributes Configuration\n".
           "section.  On some systems, you may also have the option of using SMH.\n\n";

    # First, check for possible conflicts and corner cases

    # check nsswitch for possible conflicts
    my $nsswitch = &getGlobal('FILE', 'nsswitch.conf');
    if ( -e $nsswitch) {
        open(FILE, $nsswitch);
        while (<FILE>) {
            if (/nis/ or /compat/ or /ldap/) {
              my $message = "Bastille found a possible conflict between trusted mode and\n" .
		            "$nsswitch.  Please remove all references to\n" .
                            "\"compat\", \"nis\" and \"ldap\" in $nsswitch\n" .
                            "and rerun Bastille, or use SAM/SMH to\n" .
                            "$specialinstructions\n".
                            "$saminstructions";
              close(FILE);
	      return (0,$message);
            }
        }
        close(FILE);
    }

    # check the namesvrs config file for possible NIS conflicts
    #Changed to unless "Y AND Y" since question can be skipped when nis is off
    # but corner cases can still exist, so check then too.
    unless ( &getGlobalConfig('MiscellaneousDaemons','nis_client') eq "Y" and
         &getGlobalConfig('MiscellaneousDaemons','nis_server') eq "Y" ) {
	my $namesvrs = &getGlobal('FILE', 'namesvrs');
	if (open(FILE, $namesvrs)) {
	    while (<FILE>) {
		if (/^NIS.*=["]?1["]?$/) {
		    my $message= "Possible conflict between trusted mode and NIS found.\n".
			"Please use SAM/SMH to\n" .
			"  - turn off NIS\n" .
			"$specialinstructions\n".
			"$saminstructions";
		    close(FILE);
		    return (0,$message);
		}
	    }
	    close(FILE);
	} else {
            &B_log("ERROR","Unable to open $namesvrs for reading.");
            my $message= "Possible conflict between trusted mode and NIS found.\n".
		"Please use SAM/SMH to\n" .
		"  - turn off NIS\n" .
		"$specialinstructions\n".
		"$saminstructions";
	    return (0,$message);
	}
	if ( &B_match_line (&getGlobal("FILE","passwd"),"^\+:.*")) {
	    my $message= '"+" entry found in passwd file.  These are not\n' .
	    "compatible with Trusted Mode.  Either remove the entries\n" .
	    "and re-run Bastille, or re-run Bastille, and direct it to\n" .
	    "disable NIS client and server.\n";
	    return (0,$message);
	    }

    }


    # check for conflicts with DCE integrated login
    my $authcmd = &getGlobal('BIN','auth.adm');
    if ( -e $authcmd ) {
         my $retval = system("PATH=/usr/bin $authcmd -q 1>/dev/null 2>&1");
         if ($retval != 0 and $retval != 1) {
             my $message="It appears that DCE integrated login is configured on this system.\n" .
		      "DCE integrated login is incompatible with trusted systems and\n" .
		      "auditing.  Bastille is unable to\n" .
		      "$specialinstructions" .
		      "You will need to configure auditing and password policies using DCE.\n\n";
	     return (0,$message);
         }
    }

    if ( -e &getGlobal('FILE','shadow') ) {
       my $message="This system has already been converted to shadow passwords.\n" .
                   "Shadow passwords are incompatible with trusted mode.\n" .
		   "Bastille is unable to\n" .
		   "$specialinstructions" .
                   "If you desire these features, you should use\n".
                   "\'pwunconv\' to change back to standard passwords,\n".
                   "and then rerun Bastille.\n\n";
       return (0,$message);
   }

    return (1,$saminstructions);
}

# This routine allows Bastille to determine trusted-mode extension availability

sub convertToShadow {

        if (&isSystemTrusted) {
            # This is an internal error...Bastille should not call this routine
            # in this case.  Error is here for robustness against future changes.
            &B_log("ERROR","This system is already converted to trusted mode.\n" .
                      "         Converting to shadow passwords will not be attempted.\n");
            return 0;
        }

	# configuration files on which shadowed passwords depend
        my $nsswitch_conf = &getGlobal('FILE',"nsswitch.conf");

	# binaries used to convert to a shadowed password
	my $pwconv = &getGlobal('BIN',"pwconv");
	my $echo = &getGlobal('BIN','echo'); # the echo is used to pipe a yes into the pwconv program as
	                                     # pwconv requires user interaction.

	# the binary used in a system revert.
	my $pwunconv = &getGlobal('BIN',"pwunconv");
	#check the password file for nis usage and if the nis client
	#or server is running.
	if(-e $nsswitch_conf) {
	    # check the file for nis, nis+, compat, or dce usage.
	    if(&B_match_line($nsswitch_conf, '^\s*passwd:.+(nis|nisplus|dce|compat)')) {
		my $shadowTODO = "\n---------------------------------\nHide encrypted passwords:\n" .
		                 "---------------------------------\n" .
		                 "This version of password shadowing does not support any repository other\n" .
		                 "than files. In order to convert your password database to shadowed passwords\n" .
				 "there can be no mention of nis, nisplus, compat, or dce in the passwd\n" .
				 "field of the \"$nsswitch_conf\" file.  Please make the necessary edits to\n" .
				 "the $nsswitch_conf file and run Bastille again using the command:\n" .
				 "\"bastille -b\"\n";
		# Adding the shadowTODO comment to the TODO list.
		&B_TODO("$shadowTODO");
		# Notifing the user that the shadowed password coversion has failed.
		&B_log("ERROR","Password Shadowing Conversion Failed\n" .
			  "$shadowTODO");
		# exiting the subroutine.
		return 0;
	    }

	}

	# convert the password file to a shadowed repository.
        if (( -e $pwconv ) and ( -e $pwunconv ) and
            ( &B_System("$echo \"yes\" | $pwconv","$pwunconv") ) ){
	    &B_TODO( "\n---------------------------------\nShadowing Password File:\n" .
		     "---------------------------------\n" .
		     "Your password file has been converted to use password shadowing.\n" .
		     "This version of password shadowing does not support any repository other\n" .
		     "than files. There can be no mention of nis, nisplus, compat, or dce\n" .
		     "in the passwd field of the \"$nsswitch_conf\" file.\n\n" );
	} else {
            &B_log("ERROR","Conversion to shadow mode failed.  The system may require ".
                   "a patch to be capable of switching to shadow mode, or the ".
                   "system my be in a state where conversion is not possible.");
        }
}



##########################################################################
# &getSupportedSettings();
# Manipulates %trustedParameter and %isSupportedSetting, file-scoped variables
#
# Reads the password policy support matrix, which in-turn gives Bastille the
# places it should look for a given password policy setting.

# Note the file was created like this so if could be maintained in an Excel(tm)
# spreadsheet, to optimize reviewability.  TODO: consider other formats

#  File Format:
#  HEADERS:<comment>,[<OS Version> <Mode> <Extensions>,]...
#  [
#  :<label>:<trusted equivalent>,,,,,,,,,,,,<comment>
#  <action> (comment), [<test value>,]...
#  ] ...
# Example;
# HEADERS:Information Source (trusted equiv),11.11 Standard no-SMSE,11.11 Trusted no-SMSE,11.11 Shadow no-SMSE,11.23 Standard no-SMSE,11.23 Trusted no-SMSE,11.23 Shadow no-SMSE,11.23 Standard SMSE,11.23 Shadow SMSE,11.23 Trusted SMSE,11.31 Trusted SMSE,11.31 Shadow SMSE,11.31 Standard SMSE,Other Exceptions
#:ABORT_LOGIN_ON_MISSING_HOMEDIR,,,,,,,,,,,,,root
#/etc/security.dsc (search),x,,xx,x,x,x,!,!,!,!,!,!,
#/etc/default/security(search),y,y,y,y,y,y,y,y,y,y,y,y,
#getprdef (execute with <Trusted Equiv> argument),x,x,x,x,x,x,x,x,x,x,x,x,

###########################################################################
our %trustedParameter = ();
our %isSupportedSetting = ();

sub getSupportedSettings() {

    my $line; # For a config file line
    my $linecount = 0;
    my $currentsetting = "";
    my @fields; # Fields in a given line
    my @columns; #Column Definitions


    &B_open(*SETTINGSFILE,&getGlobal('BFILE','AccountSecSupport'));
    my @settingLines=<SETTINGSFILE>;
    &B_close(*SETTINGSFILE);

    #Remove blank-lines and comments
    @settingLines = grep(!/^#/,@settingLines);
    @settingLines = grep(!/^(\s*,+)*$/,@settingLines);

    foreach $line (@settingLines) {
	++$linecount;
	@fields = split(/,/,$line);
	if ($line =~ /^Information Source:/) { #Sets up colums
	    my $fieldcount = 1; #Skipping first field
	    while ((defined($fields[$fieldcount])) and
                   ($fields[$fieldcount] =~ /\d+\.\d+/)){
		my @subfields = split(/ /,$fields[$fieldcount]);
                my $fieldsCount = @subfields;
                if ($fieldsCount != 3){
                    &B_log("ERROR","Invalid subfield count: $fieldsCount in:".
                           &getGlobal('BFILE','AccountSecSupport') .
                           " line: $linecount and field: $fieldcount");
                }
		$columns[$fieldcount] = {OSVersion => $subfields[0],
                                         Mode => $subfields[1],
                                         Extension => $subfields[2] };
                &B_log("DEBUG","Found Header Column, $columns[$fieldcount]{'OSVersion'}, ".
                       $columns[$fieldcount]{'Mode'} ." , " .
                       $columns[$fieldcount]{'Extension'});
		++$fieldcount;
		}                                      # New Account Seting ex:
	} elsif ($line =~ /^:([^,:]+)(?::([^,]+))?/) { # :PASSWORD_WARNDAYS:expwarn,,,,,,,,,,,,
	    $currentsetting = $1;
	    if (defined($2)) {
		$trustedParameter{"$currentsetting"}=$2;
	    }
            &B_log("DEBUG","Found Current Setting: ". $currentsetting .
                   "/" . $trustedParameter{"$currentsetting"});
	} elsif (($line =~ /(^[^, :\)\(]+)[^,]*,((?:(?:[!y?nx]|!!),)+)/) and #normal line w/ in setting ex:
		 ($currentsetting ne "")){ # security.dsc (search),x,x,x,x,x,!,!!,!,!,!,!,
	    my $placeToLook = $1;
	    my $fieldcount = 1; #Skip the first one, which we used in last line
	    while (defined($fields[$fieldcount])) {
		&B_log("DEBUG","Setting $currentsetting : $columns[$fieldcount]{OSVersion} , ".
		       "$columns[$fieldcount]{Mode} , ".
		       "$columns[$fieldcount]{Extension} , ".
		       "$placeToLook, to $fields[$fieldcount]");
		$isSupportedSetting{"$currentsetting"}
		    {"$columns[$fieldcount]{OSVersion}"}
		    {"$columns[$fieldcount]{Mode}"}
		    {"$columns[$fieldcount]{Extension}"}
		    {"$placeToLook"} =
		    $fields[$fieldcount];
                    ++$fieldcount;
	    }
	} else {
	    if ($line !~ /^,*/) {
                &B_log("ERROR","Incorrectly Formatted Line at ".
                       &getGlobal('BFILE','AccountSecSupport') . ": $linecount");
            }
	}
    }
}

##########################################################################
# &B_get_sec_value($param);
# This subroutine finds the value for a given user policy parameter.
# Specifically, it supports the parameters listed in the internal data structure

# Return values:
# 'Not Defined' if the value is not present or not uniquely defined.
# $value if the value is present and unique
#
###########################################################################
sub B_get_sec_value($) {
    my $param=$_[0];

    my $os_version;
    if (&GetDistro =~ /^HP-UX\D*(\d+\.\d+)/ ){
	$os_version=$1;
    } else {
	&B_log("ERROR","B_get_sec_value only supported on HP-UX");
	return undef;
    }
#    my $sec_dsc =  &getGlobal('FILE', 'security.dsc');
    my $sec_file = &getGlobal('FILE', 'security');
    my $getprdef = &getGlobal('BIN','getprdef');
    my $getprpw = &getGlobal('BIN','getprpw');
    my $userdbget = &getGlobal('BIN','userdbget');
    my $passwd = &getGlobal('BIN','passwd');

    my $sec_flags = "";
    my @sec_settings=();
    my $user_sec_setting="";

    my $security_mode="Standard";
    my $security_extension="no-SMSE";

    &B_log("DEBUG","Entering get_sec_value for: $param");

    sub isok ($) { # Locally-scoped subroutine, takes supported-matrix entry as argument
	my $supportedMatrixEntry = $_[0];

	if ($supportedMatrixEntry =~ /!/) { #Matrix Entry for "Documented and/or tested"
           &B_log("DEBUG","isOk TRUE: $supportedMatrixEntry");
	    return 1;
	} else {
            &B_log("DEBUG","isOk FALSE: $supportedMatrixEntry");
	    return 0; #FALSE
	}
    } #end local subroutine

    #Get Top Array item non-destructively
    sub getTop (@) {
        my @incomingArray = @_;
        my $topval = pop(@incomingArray);
        push(@incomingArray,$topval); #Probably redundant, but left in just in case.
        return $topval;
    }

    sub ifExistsPushOnSecSettings($$) {
        my $sec_settings = $_[0];
        my $pushval = $_[1];

        if ($pushval ne ""){
            push (@$sec_settings, $pushval);
        }
    }

    #prpw and prdef both use "YES" instead of "1" like the other settings.
    sub normalizePolicy($){
        my $setting = $_[0];

        $setting =~ s/YES/1/;
        $setting =~ s/NO/1/;

        return $setting;
    }



    if ((%trustedParameter == ()) or (%isSupportedSetting == ())) {
	# Manipulates %trustedParameter and %isSupportedSetting
	&getSupportedSettings;
    }

    #First determine the security mode
    my $shadowFile = &getGlobal("FILE","shadow");
    my $passwdFile = &getGlobal("FILE","passwd");

    if (&isSystemTrusted) {
	$security_mode = 'Trusted';
    } elsif ((-e $shadowFile) and #check file exist, and that passwd has no non-"locked" accounts
             (not(&B_match_line($passwdFile,'^[^\:]+:[^:]*[^:*x]')))) {
	    $security_mode = 'Shadow';
    } else {
	$security_mode = 'Standard';
    }
    if (&isTrustedMigrationAvailable) {
	$security_extension = 'SMSE';
	} else {
	$security_extension = 'no-SMSE';
    }
    &B_log("DEBUG","Security mode: $security_mode extension: $security_extension");
    # Now look up the value from each applicable database, from highest precedence
    # to lowest:
    &B_log("DEBUG","Checking $param in userdbget");
    if (&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
              {$security_extension}{"userdbget_-a"})) {
	&ifExistsPushOnSecSettings(\@sec_settings,
                                   &B_getValueFromString('\w+\s+\w+=(\S+)',
                                                         &B_Backtick("$userdbget -a $param")));
        &B_log("DEBUG", $param . ":userdbget setting: ". &getTop(@sec_settings));
    }
    &B_log("DEBUG","Checking $param in passwd");
    if (&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
              {$security_extension}{"passwd_-sa"})) {
	if ($param eq "PASSWORD_MINDAYS") {
	    &ifExistsPushOnSecSettings(\@sec_settings,
                                       &B_getValueFromString('(?:\w+\s+){2}[\d\/]+\s+(\d+)\s+\d+',
                                                             &B_Backtick("$passwd -s -a")));
	} elsif ($param eq "PASSWORD_MAXDAYS") {
	    &ifExistsPushOnSecSettings(\@sec_settings,
                                       &B_getValueFromString('(?:\w+\s+){2}[\d\/]+\s+\d+\s+(\d+)',
                                                             &B_Backtick("$passwd -s -a")));
	} elsif ($param eq "PASSWORD_WARNDAYS") {
	    &ifExistsPushOnSecSettings(\@sec_settings,
                                       &B_getValueFromString('(?:\w+\s+){2}[\d\/]+(?:\s+\d+){2}\s+(\d+)',
                                                             &B_Backtick("$passwd -s -a")));
	}
        &B_log("DEBUG", $param . ":passwd -sa setting: ". &getTop(@sec_settings));
    }
    &B_log("DEBUG","Checking $param in get prpw");
    if (&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
              {$security_extension}{"getprpw"})) {
        my $logins = &getGlobal("BIN","logins");
	my @userArray = split(/\n/,`$logins`);
	my $userParamVals = '';
	foreach my $rawuser (@userArray) {
            $rawuser =~ /^(\S+)/;
	    my $user = $1;
            my $nextParamVal=&B_Backtick("$getprpw -l -m $trustedParameter{$param} $user");
            $nextParamVal =~ s/\w*=(-*[\w\d]*)/$1/;
	    if ($nextParamVal != -1) { #Don't count users for which the local DB is undefined
                $userParamVals .= $user . "::::" . $nextParamVal ."\n";
            }
	} #Note getValueFromStrings deals with duplicates, returning "Not Unigue"
        my $policySetting = &B_getValueFromString('::::(\S+)',"$userParamVals");
	&ifExistsPushOnSecSettings (\@sec_settings, &normalizePolicy($policySetting));
        &B_log("DEBUG", $param . ":prpw setting: ". &getTop(@sec_settings));
    }
    &B_log("DEBUG","Checking $param in get prdef");
    if (&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
              {$security_extension}{"getprdef"})) {
	$_ = &B_Backtick ("$getprdef -m " . $trustedParameter{$param});
	/\S+=(\S+)/;
        my $policySetting = $1;
	&ifExistsPushOnSecSettings(\@sec_settings, &normalizePolicy($policySetting));
        &B_log("DEBUG", $param . ":prdef setting: ". &getTop(@sec_settings));

    }
    &B_log("DEBUG","Checking $param in default security");
    if (&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
              {$security_extension}{"/etc/default/security"})) {
	&ifExistsPushOnSecSettings(\@sec_settings,&B_getValueFromFile('^\s*'. $param .
                                               '\s*=\s*([^\s#]+)\s*$', $sec_file));
        &B_log("DEBUG", $param . ":default setting: ". &getTop(@sec_settings));
    }
    #Commented below code in 3.0 release to avoid implication that bastille
    #had ever set these values explicitly, and the implications to runnable
    #config files where Bastille would then apply the defaults as actual policy
    #with possible conversion to shadow or similar side-effect.

#    &B_log("DEBUG","Checking $param in security.dsc");
    #security.dsc, only added in if valid for OS/mode/Extension, and nothing else
    #is defined (ie: @sec_settings=0)
#    if ((&isok($isSupportedSetting{$param}{$os_version}{$security_mode}
#              {$security_extension}{"/etc/security.dsc"})) and (@sec_settings == 0)) {
#	&ifExistsPushOnSecSettings(\@sec_settings, &B_getValueFromFile('^' . $param .
#                                                ';(?:[-\w/]*;){2}([-\w/]+);', $sec_dsc));
#        &B_log("DEBUG", $param . ":security.dsc: ". &getTop(@sec_settings));
#    }

    # Return what we found
    my $last_setting=undef;
    my $current_setting=undef;
    while (@sec_settings > 0) {
	$current_setting = pop(@sec_settings);
        &B_log("DEBUG","Comparing $param configuration for identity: " .
               $current_setting);
	if ((defined($current_setting)) and ($current_setting ne '')) {
	    if (not(defined($last_setting))){
		$last_setting=$current_setting;
	    } elsif (($last_setting ne $current_setting) or
                     ($current_setting eq 'Not Unique')){
                &B_log("DEBUG","$param setting not unique.");
		return 'Not Unique';  # Inconsistent state found, return 'Not Unique'
	    }
	}
    }
    if ((not(defined($last_setting))) or ($last_setting eq '')) {
        return undef;
    } else {
        return $last_setting;
    }

} #End B_get_sec_value

sub secureIfNoNameService($){
    my $retval = $_[0];
    
    if (&isUsingRemoteNameService) {
        return MANUAL();
    } else {
        return $retval;
    }
}

#Specifically for cleartext protocols like NIS, which are not "secure"
sub isUsingRemoteNameService(){
    
    if (&remoteServiceCheck('nis|nisplus|dce') == SECURE_CAN_CHANGE()){
        return 0; #false
    } else {
        return 1;
    }
}



###########################################
## This is a wrapper for two functions that
## test the existence of nis-like configurations
## It is used by both the front end test and the back-end run
##############################################
sub remoteServiceCheck($){
        my $regex = $_[0];
        
        my $nsswitch_conf = &getGlobal('FILE',"nsswitch.conf");
        my $passwd = &getGlobal('FILE',"passwd");
        
        # check the file for nis usage.
        if (-e $nsswitch_conf) {
            if (&B_match_line($nsswitch_conf, '^\s*passwd:.*('. $regex . ')')) {
                    return NOTSECURE_CAN_CHANGE();
            } elsif ((&B_match_line($nsswitch_conf, '^\s*passwd:.*(compat)')) and
            (&B_match_line($passwd, '^\s*\+'))) {
                    return NOTSECURE_CAN_CHANGE(); # true
            }
        } elsif ((&B_match_line($passwd, '^\s*\+'))) {
                return NOTSECURE_CAN_CHANGE();
        }
        
        my $oldnisdomain=&B_get_rc("NIS_DOMAIN");
        if ((($oldnisdomain eq "") or ($oldnisdomain eq '""')) and (&checkServiceOnHPUX('nis.client'))){
            return SECURE_CAN_CHANGE();
        }
        return NOTSECURE_CAN_CHANGE();
}

#############################################
# remoteNISPlusServiceCheck
# test the existence of nis+ configuration
#############################################
sub remoteNISPlusServiceCheck () {

    my $nsswitch_conf = &getGlobal('FILE',"nsswitch.conf");

    # check the file for nis+ usage.
    if (-e $nsswitch_conf) {
        if (&B_match_line($nsswitch_conf, 'nisplus')) {
            return NOTSECURE_CAN_CHANGE();
        }
    }

    return &checkServiceOnHPUX('nisp.client');
}


##########################################################################
# This subroutine creates nsswitch.conf file if the file not exists,
# and then append serveral services into the file if the service not
# exists in the file.
##########################################################################
sub B_create_nsswitch_file ($) {
    my $regex = $_[0];

    my $nsswitch = &getGlobal('FILE',"nsswitch.conf");

    if( ! -f $nsswitch ) {
        &B_create_file($nsswitch);
        # we don't need to revert the permissions change because we just
        # created the file
        chmod(0444, $nsswitch);

        &B_append_line($nsswitch,'\s*passwd:', "passwd:       $regex\n");
        &B_append_line($nsswitch,'\s*group:', "group:        $regex\n");
        &B_append_line($nsswitch,'\s*hosts:', "hosts:        $regex\n");
        &B_append_line($nsswitch,'\s*networks:', "networks:     $regex\n");
        &B_append_line($nsswitch,'\s*protocols:', "protocols:    $regex\n");
        &B_append_line($nsswitch,'\s*rpc:', "rpc:          $regex\n");
        &B_append_line($nsswitch,'\s*publickey:', "publickey:    $regex\n");
        &B_append_line($nsswitch,'\s*netgroup:', "netgroup:     $regex\n");
        &B_append_line($nsswitch,'\s*automount:', "automount:    $regex\n");
        &B_append_line($nsswitch,'\s*aliases:', "aliases:      $regex\n");
        &B_append_line($nsswitch,'\s*services:', "services:     $regex\n");
    }
}

1;

