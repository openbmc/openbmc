# Copyright (C) 1999-2007 Jay Beale
# Copyright (C) 2001-2008 Hewlett-Packard Development Company, L.P.
# Licensed under the GNU General Public License, version 2

package Bastille::API;

## TO DO:
#
#
#   1) Look for more places to insert error handling...
#
#   2) Document this module more
#
#


##########################################################################
#
# This module forms the basis for the v1.1 API.
#
 ##########################################################################

#
# This module forms the initial basis for the Bastille Engine, implemented
# presently via a Perl API for Perl modules.
#
# This is still under construction -- it is very usable, but not very well
# documented, yet.
#

##########################################################################
#
#                          API Function Listing
#
##########################################################################
# The routines which should be called by Bastille modules are listed here,
# though they are better documented throughout this file.
#
# Distro Specific Stuff:
#
#  &GetDistro     - figures out what distro we're running, if it knows it...
#  &ConfigureForDistro - sets global variables based on the distro
#  &GetGlobal - returns hash values defined in ConfigureForDistro
#
#  &getGlobalConfig - returns value of hash set up by ReadConfig
#
# Logging Specific Stuff has moved to LogAPI.pm:
#
#  &B_log(type,msg) -- takes care of all logging
#
#
# Input functions for the old input method...
#
# File open/close/backup functions
#
#  &B_open     * -- opens a file handle and logs the action/error (OLD WAY!)
#  &B_open_plus  -- opens a pair of file handles for the old and new version
#                   of a file; respects logonly flag.  (NEW WAY)
#  &B_close    * -- closes a file handle and logs the action/error (OLD WAY!)
#  &B_close_plus -- closes a pair of file handles opened by B_open_plus,
#                   backing up one file and renaming the new file to the
#                   old one's name, logging actions/errors.  Respects the
#                   logonly flag -- needs B_backup file.  Finally, sets
#                   new file's mode,uid,gid to old file's...  (NEW WAY)
#  &B_backup_file - backs up a file that is being changed/deleted into the
#                   $GLOBAL_BDIR{"backup"} directory.
#
# Non-content file modification functions
#
#  &B_delete_file - deletes the named file, backing up a copy
#  &B_create_file - creates the named file, if it doesn't exist
#
#  &B_symlink     	- create a symlink to a file, recording the revert rm
#
# More stuff
#
#  &B_createdir     - make a directory, if it doesn't exist, record revert rmdir
#  &B_cp            - copy a file, respecting LOGONLY and revert func.
#  &B_mknod         - wrap mknod with revert and logonly and prefix functionality
#
#  &B_read_sums     - reads sum.csv file and parses input into the GLOBAL_SUM hash
#  &B_write_sums    - writes sum.csv file from GLOBAL_SUM hash
#  &B_check_sum($)  - take a file name and compares the stored cksum with the current
#                     cksum of said file
#  &B_set_sum($)    - takes a file name and gets that files current cksum then sets
#                     that sum in the GLOBAL_SUM hash
#  &B_revert_log - create entry in shell script, executed later by bastille -r
#  &showDisclaimer  - Print the disclaimer and wait for 5 minutes for acceptance
###########################################################################
# Note:  GLOBAL_VERBOSE
#
# All logging functions now check GLOBAL_VERBOSE and, if set, will print
# all the information sent to log files to STDOUT/STDERR as well.
#

#
# Note:  GLOBAL_LOGONLY
#
# All Bastille API functions now check for the existence of a GLOBAL_LOGONLY
# variable.  When said variable is set, no function actually modifies the
# system.
#
# Note:  GLOBAL_DEBUG
#
# The B_log("DEBUG",...) function now checks GLOBAL_DEBUG and, if set, it will
# print all the information to a new debug-log file. If GLOBAL_VERBOSE is
# set it might log to STDOUT/STDERR as well (not yet implemented, pending
# discussion). Developers should populate appropriate places with &B_log(DEBUG)
# in order to be able to tell users to use this options and send the logs
# for inspection and debugging.
#
#


# Libraries for the Backup_file routine: Cwd and File::Path
use Cwd;
use Bastille::OSX_API;
use Bastille::LogAPI;
use File::Path;
use File::Basename;

# Export the API functions listed below for use by the modules.

use Exporter;
@ISA = qw ( Exporter );
@EXPORT = qw(
    setOptions  GetDistro ConfigureForDistro B_log B_revert_log
    SanitizeEnv
    B_open B_close B_symlink StopLogging
    B_open_plus B_close_plus
    B_isFileinSumDB
    B_create_file B_read_sums B_check_sum  B_set_sum isSumDifferent listModifiedFiles
    B_create_dir B_create_log_file
    B_delete_file
    B_cp B_place B_mknod
    showDisclaimer 
    getSupportedOSHash 
    B_Backtick
    B_System
    isProcessRunning
    checkProcsForService
    
    
    $GLOBAL_OS $GLOBAL_ACTUAL_OS $CLI
    $GLOBAL_LOGONLY $GLOBAL_VERBOSE $GLOBAL_DEBUG $GLOBAL_AUDITONLY $GLOBAL_AUDIT_NO_BROWSER $errorFlag
    %GLOBAL_BIN %GLOBAL_DIR %GLOBAL_FILE
    %GLOBAL_BDIR %GLOBAL_BFILE
    %GLOBAL_CONFIG %GLOBAL_SUM

    %GLOBAL_SERVICE %GLOBAL_SERVTYPE %GLOBAL_PROCESS %GLOBAL_RC_CONFIG
    %GLOBAL_TEST
    
    getGlobal setGlobal getGlobalConfig
    
    
    B_parse_fstab
    B_parse_mtab B_is_rpm_up_to_date 
    
    NOTSECURE_CAN_CHANGE SECURE_CANT_CHANGE  
    NOT_INSTALLED  INCONSISTENT MANUAL NOTEST SECURE_CAN_CHANGE
    STRING_NOT_DEFINED NOT_INSTALLED_NOTSECURE DONT_KNOW
    RELEVANT_HEADERQ NOTRELEVANT_HEADERQ
);



######################################################
###Testing Functions
##################################################################

#Define "Constants" for test functions.  Note these constants sometimes get
#interpreted as literal strings when used as hash references, so you may
# have to use CONSTANT() to disambiguate, like below.  Sorry, it was either
# that or create even *more* global variables.
# See TestDriver.pm for definitions, and test design doc for full explaination
use constant {
    NOTSECURE_CAN_CHANGE => 0,
    SECURE_CANT_CHANGE     => 1,
    NOT_INSTALLED => 2, # (where the lack makes the system secure, eg telnet)
    INCONSISTENT => 3,
    MANUAL => 4,
    NOTEST => 5,
    SECURE_CAN_CHANGE => 6,
    STRING_NOT_DEFINED => 7,
    NOT_INSTALLED_NOTSECURE => 8, #(Where the missing s/w makes the system less secure eg IPFilter)
    #Intentional duplicates follow
    DONT_KNOW => 5,
    RELEVANT_HEADERQ => 6,
    NOTRELEVANT_HEADERQ => 0
};

&SanitizeEnv;

# Set up some common error messages.  These are independent of
# operating system

# These will allow us to line up the warnings and error messages
my $err ="ERROR:  ";
my $spc ="        ";
my $GLOBAL_OS="None";
my $GLOBAL_ACTUAL_OS="None";
my %GLOBAL_SUMS=();
my $CLI='';

#OS independent Error Messages Follow, normally "bastille" script filters
#options before interactive or Bastille runs, so this check is often redundant
$GLOBAL_ERROR{"usage"}="\n".
    "$spc Usage: bastille [ -b | -c | -x ] [ --os <version> ] [ -f <alternate config> ]\n".
    "$spc        bastille [ -r | --assess | --assessnobowser ]\n\n".
    "$spc --assess : check status of system and report in browser\n".
    "$spc --assessnobrowser : check status of system and list report locations\n".
    "$spc -b : use a saved config file to apply changes\n".
    "$spc      directly to system\n".
    "$spc -c : use the Curses (non-X11) TUI\n".
    "$spc -f <alternate config>: populate answers with a different config file\n".
    "$spc -r : revert all Bastille changes to-date\n".
    "$spc -x : use the Perl/Tk (X11) GUI\n" .
    "$spc --os <version> : ask all questions for the given operating system\n" .
    "$spc                version.  e.g. --os RH6.0\n";

# These options don't work universally, so it's best not to
# document them here (yet).  Hopefully, we'll get them
# straightened out soon.
#"$spc --log : log-only option\n".
#"$spc -v : verbose mode\n".
#"$spc --debug : debug mode\n";


##############################################################################
#
#  Directory structure for Bastille Linux v1.2 and up
#
##############################################################################
#
#  /usr/sbin/          -- location of Bastille binaries
#  /usr/lib/Bastille   -- location of Bastille modules
#  /usr/share/Bastille -- location of Bastille data files
#  /etc/Bastille       -- location of Bastille config files
#
#  /var/log/Bastille      -- location of Bastille log files
#  /var/log/Bastille/revert -- directory holding all Bastille-created revert scripts
#  /var/log/Bastille/revert/backup -- directory holding the original files that
#                                   Bastille modifies, with permissions intact
#
##############################################################################

##############################################################################
#
#  Directory structure for HP-UX Bastille v2.0 and up
#
##############################################################################
#
#  /opt/sec_mgmt/bastille/bin/  -- location of Bastille binaries
#  /opt/sec_mgmt/bastille/lib/  -- location of Bastille modules
#  /etc/opt/sec_mgmt/bastille/  -- location of Bastille data and config files
#
#  /var/opt/sec_mgmt/bastille/log/   -- location of Bastille log files
#  /var/opt/sec_mgmt/bastille/revert -- directory holding all Bastille-created
#                                       revert scripts and save files
#
##############################################################################


##############################################################################
##############################################################################
##################  Actual functions start here... ###########################
##############################################################################
##############################################################################

###########################################################################
# setOptions takes six arguments, $GLOBAL_DEBUG, $GLOBAL_LOGONLY,
# $GLOBAL_VERBOSE, $GLOBAL_AUDITONLY, $GLOBAL_AUDIT_NO_BROWSER, and GLOBAL_OS;
###########################################################################
sub setOptions($$$$$$) {
    ($GLOBAL_DEBUG,$GLOBAL_LOGONLY,$GLOBAL_VERBOSE,$GLOBAL_AUDITONLY,
     $GLOBAL_AUDIT_NO_BROWSER,$GLOBAL_OS) = @_;
    if ($GLOBAL_AUDIT_NO_BROWSER) {
	$GLOBAL_AUDITONLY = 1;
    }
    if (not(defined($GLOBAL_OS))){
        $GLOBAL_OS="None";
    }
}
###########################################################################
#
# SanitizeEnv load a proper environment so Bastille cannot be tricked
# and Perl modules work correctly.
#
###########################################################################
sub SanitizeEnv {
	 delete @ENV{'IFS','CDPATH','ENV','BASH_ENV'};
	 $ENV{CDPATH}=".";
	 $ENV{BASH_ENV}= "";
	 # Bin is needed here or else  /usr/lib/perl5/5.005/Cwd.pm
	 # will not find `pwd`
	 # Detected while testing with -w, jfs
	 $ENV{PATH} = "/bin:/usr/bin";
	 # Giorgi, is /usr/local/bin needed? (jfs)
}

###########################################################################
#
# GetDistro checks to see if the target is a known distribution and reports
# said distribution.
#
# This is used throughout the script, but also by ConfigureForDistro.
#
#
###########################################################################

sub GetDistro() {

    my ($release,$distro);

    # Only read files for the distro once.
    # if the --os option was used then
    if ($GLOBAL_OS eq "None") {
	if ( -e "/etc/mandrake-release" ) {
	    open(MANDRAKE_RELEASE,"/etc/mandrake-release");
	    $release=<MANDRAKE_RELEASE>;

	    if ( ($release =~ /^Mandrake Linux release (\d+\.\d+\w*)/) or ($release =~ /^Linux Mandrake release (\d+\.\d+\w*)/) ) {
		$distro="MN$1";
	    }
	    elsif ( $release =~ /^Mandrakelinux release (\d+\.\d+)\b/ ) {
                $distro="MN$1";
            }
            else {
		print STDERR "$err Couldn't determine Mandrake/Mandriva version! Setting to 10.1!\n";
		$distro="MN10.1";
	    }

	    close(MANDRAKE_RELEASE);
	}
	elsif ( -e "/etc/immunix-release" ) {
	    open(IMMUNIX_RELEASE,"/etc/immunix-release");
	    $release=<IMMUNIX_RELEASE>;
	    unless ($release =~ /^Immunix Linux release (\d+\.\d+\w*)/) {
		print STDERR "$err Couldn't determine Immunix version! Setting to 6.2!\n";
		$distro="RH6.2";
	    }
	    else {
		$distro="RH$1";
	    }
	    close(*IMMUNIX_RELEASE);
	}
	elsif ( -e '/etc/fedora-release' ) {
            open(FEDORA_RELEASE,'/etc/fedora-release');
            $release=<FEDORA_RELEASE>;
            close FEDORA_RELEASE;
            if ($release =~ /^Fedora Core release (\d+\.?\d*)/) {
                $distro = "RHFC$1";
            }
	    elsif ($release =~ /^Fedora release (\d+\.?\d*)/) {
                $distro = "RHFC$1";
            } 
            else {
                print STDERR "$err Could not determine Fedora version! Setting to Fedora Core 8\n";
                $distro='RHFC8';
            }
	}
	elsif ( -e "/etc/redhat-release" ) {
	    open(*REDHAT_RELEASE,"/etc/redhat-release");
	    $release=<REDHAT_RELEASE>;
	    if ($release =~ /^Red Hat Linux release (\d+\.?\d*\w*)/) {
		$distro="RH$1";
	    }
            elsif ($release =~ /^Red Hat Linux .+ release (\d+)\.?\d*([AEW]S)/) {
                $distro="RHEL$1$2";
            }
	    elsif ($release =~ /^Red Hat Enterprise Linux ([AEW]S) release (\d+)/) {
		$distro="RHEL$2$1";
	    }
	    elsif ($release =~ /^CentOS release (\d+\.\d+)/) {
		my $version = $1;
		if ($version =~ /^4\./) {
		    $distro='RHEL4AS';
		}
		elsif ($version =~ /^3\./) {
		    $distro='RHEL3AS';
		}
		else {
		    print STDERR "$err Could not determine CentOS version! Setting to Red Hat Enterprise 4 AS.\n";
		    $distro='RHEL4AS';
                 }
	    }
 	    else {
		# JJB/HP - Should this be B_log?
		print STDERR "$err Couldn't determine Red Hat version! Setting to 9!\n";
		$distro="RH9";
	    }
	    close(REDHAT_RELEASE);

	}
	elsif ( -e "/etc/debian_version" ) {
	    $stable="3.1"; #Change this when Debian stable changes
	    open(*DEBIAN_RELEASE,"/etc/debian_version");
	    $release=<DEBIAN_RELEASE>;
	    unless ($release =~ /^(\d+\.\d+\w*)/) {
		print STDERR "$err System is not running a stable Debian GNU/Linux version. Setting to $stable.\n";
		$distro="DB$stable";
	    }
	    else {
		$distro="DB$1";
	    }
	    close(DEBIAN_RELEASE);
	}
	elsif ( -e "/etc/SuSE-release" ) {
	    open(*SUSE_RELEASE,"/etc/SuSE-release");
	    $release=<SUSE_RELEASE>;
	    if ($release =~ /^SuSE Linux (\d+\.\d+\w*)/i) {
		$distro="SE$1";
	    }
	    elsif ($release =~ /^SUSE LINUX Enterprise Server (\d+\.?\d?\w*)/i) {
		$distro="SESLES$1";
	    }
	    elsif ($release =~ /^SUSE Linux Enterprise Server (\d+\.?\d?\w*)/i) {
		$distro="SESLES$1";
	    }
            elsif ($release =~ /^openSuSE (\d+\.\d+\w*)/i) {
                $distro="SE$1";
            }
	    else {
		print STDERR "$err Couldn't determine SuSE version! Setting to 10.3!\n";
		$distro="SE10.3";
	    }
	    close(SUSE_RELEASE);
	}
	elsif ( -e "/etc/turbolinux-release") {
	    open(*TURBOLINUX_RELEASE,"/etc/turbolinux-release");
	    $release=<TURBOLINUX_RELEASE>;
	    unless ($release =~ /^Turbolinux Workstation (\d+\.\d+\w*)/) {
		print STDERR "$err Couldn't determine TurboLinux version! Setting to 7.0!\n";
		$distro="TB7.0";
	    }
	    else {
		$distro="TB$1";
	    }
	    close(TURBOLINUX_RELEASE);
	}
	else {
	    # We're either on Mac OS X, HP-UX or an unsupported O/S.
            if ( -x '/usr/bin/uname') {
		# uname is in /usr/bin on Mac OS X and HP-UX
		$release=`/usr/bin/uname -sr`;
	    }
	    else {
	 	print STDERR "$err Could not determine operating system version!\n";
		$distro="unknown";
            }

	    # Figure out what kind of system we're on.
	    if ($release ne "") {
		if ($release =~ /^Darwin\s+(\d+)\.(\d+)/) {
		    if ($1 == 6 ) {
			$distro = "OSX10.2";
		    }
		    elsif ($1 == 7) {
			$distro = "OSX10.3";
		    }
                    elsif ($1 == 8) {
                        $distro = "OSX10.3";
                    }
		    else {
		        $distro = "unknown";
		    }
		}
	        elsif ( $release =~ /(^HP-UX)\s*B\.(\d+\.\d+)/ ) {
		   $distro="$1$2";
		}
		else {
		   print STDERR "$err Could not determine operating system version!\n";
	           $distro="unknown";
		}
	    }
	}

	$GLOBAL_OS=$distro;
    } elsif (not (defined $GLOBAL_OS)) {
        print "ERROR: GLOBAL OS Scoping Issue\n";
    } else {
        $distro = $GLOBAL_OS;
    }

    return $distro;
}

###################################################################################
#   &getActualDistro;                                                             #
#                                                                                 #
#    This subroutine returns the actual os version in which is running on.  This  #
#    os version is independent of the --os switch feed to bastille.               #
#                                                                                 #
###################################################################################
sub getActualDistro {
    # set local variable to $GLOBAL_OS

    if ($GLOBAL_ACTUAL_OS eq "None") {
        my $os = $GLOBAL_OS;
        # undef GLOBAL_OS so that the GetDistro routine will return
        # the actualDistro, it might otherwise return the distro set
        # by the --os switch.
        $GLOBAL_OS = "None";
        $GLOBAL_ACTUAL_OS = &GetDistro;
        # reset the GLOBAL_OS variable
        $GLOBAL_OS = $os;
    }
    return $GLOBAL_ACTUAL_OS;
}
# These are helper routines which used to be included inside GetDistro
sub is_OS_supported($) {
   my $os=$_[0];
   my $supported=0;
   my %supportedOSHash = &getSupportedOSHash;

   foreach my $oSType (keys %supportedOSHash) {
       foreach my $supported_os ( @{$supportedOSHash{$oSType}} ) {
	   if ( $supported_os eq $os ) {
	       $supported=1;
	   }
       }
   }

   return $supported;
}

###############################################################################
#   getSupportedOSHash
#
#   This subrountine returns a hash of supported OSTypes, which point to a
#   a list of supported distros.  When porting to a new distro, add the
#   distro id to the hash in its appropriate list.
###############################################################################
sub getSupportedOSHash () {

    my %osHash = ("LINUX" => [
			      "DB2.2", "DB3.0",
			      "RH6.0","RH6.1","RH6.2","RH7.0",
			      "RH7.1","RH7.2","RH7.3","RH8.0",
			      "RH9",
                              "RHEL5",
			      "RHEL4AS","RHEL4ES","RHEL4WS",
			      "RHEL3AS","RHEL3ES","RHEL3WS",
			      "RHEL2AS","RHEL2ES","RHEL2WS",
			      "RHFC1","RHFC2","RHFC3","RHFC4",
			      "RHFC5","RHFC6","RHFC7","RHFC8",
			      "MN6.0","MN6.1 ","MN7.0","MN7.1",
			      "MN7.2","MN8.0","MN8.1","MN8.2",
			      "MN10.1",
			      "SE7.2","SE7.3", "SE8.0","SE8.1","SE9.0","SE9.1",
			      "SE9.2","SE9.3","SE10.0","SE10.1","SE10.2","SE10.3",
			      "SESLES8","SESLES9","SESLES10",
			      "TB7.0"
			      ],

		  "HP-UX" => [
			      "HP-UX11.00","HP-UX11.11",
			      "HP-UX11.22", "HP-UX11.23",
			      "HP-UX11.31"
			      ],

		  "OSX" => [
			    'OSX10.2','OSX10.3','OSX10.4'
			    ]
		  );

  return %osHash;

}


###############################################################################
#  setFileLocations(OSMapFile, currentDistro);
#
#  Given a file map location this subroutine will create the GLOBAL_*
#  hash entries specified within this file.
###############################################################################
sub setFileLocations($$) {

    my ($fileInfoFile,$currentDistro) = @_;

    # define a mapping from the first argument to the proper hash
    my %map = ("BIN"   => \%GLOBAL_BIN,
	       "FILE"  => \%GLOBAL_FILE,
	       "BFILE" => \%GLOBAL_BFILE,
	       "DIR"   => \%GLOBAL_DIR,
	       "BDIR"  => \%GLOBAL_BDIR
	       );
    my @fileInfo = ();

    #  File containing file location information
    if(open(FILEINFO, "<$fileInfoFile" )) {

	@fileInfo = <FILEINFO>;

	close(FILEINFO);

    }
    else {
	print STDERR "$err Unable to find file location information for '$distro'.\n" .
	    "$spc Contact the Bastille support list for details.\n";
	exit(1);
    }

    # Each line of the file map follows the pattern below:
    # bdir,init.d,'/etc/rc.d/init.d',RH7.2,RH7.3
    # if the distro information is not available, e.g.
    # bdir,init.d,'/etc/rc.d/init.d'
    # then the line applies to all distros under the OSType
    foreach my $file (@fileInfo) {
	# Perl comments are allowed within the file but only entire line comments
	if($file !~ /^\s+\#|^\s+$/) {
	    chomp $file;
	    # type relates to the map above, type bin will map to GLOBAL_BIN
	    # id is the identifier used as the hash key by the GLOBAL hash
	    # fileLocation is the full path to the file
	    # distroList is an optional list of distros that this particular
	    #   file location, if no distro list is presented the file location
	    #   is considered to apply to all distros
	    my ($type,$id,$fileLocation,@distroList) = split /\s*,\s*/, $file;
	    $fileLocation =~ s/^\'(.*)\'$/$1/;
	    if($#distroList == -1) {
		$map{uc($type)}->{$id}=$fileLocation;
	    }
	    else {
		foreach my $distro (@distroList) {
		    # if the current distro matches the distro listed then
		    # this file location applies
		    if($currentDistro =~ /$distro/) {
			$map{uc($type)}->{$id}=$fileLocation;
		    }
		}
	    }
	}
    }
    unless(defined($map{uc("BFILE")}->{"current_config"})) {
        &setGlobal("BFILE","current_config",&getGlobal("BFILE","config"));
    }
}

###############################################################################
#  setServiceInfo($OSServiceMapFile, $currentDistro
#
#  Given the location of an OS Service map file, which describes
#  a service in terms of configurables, processes and a service type.
#  The subroutine fills out the GLOBAL_SERVICE, $GLOBAL_RC_CONFIG, GLOBAL_SERVTYPE, and
#  GLOBAL_PROCESS hashes for a given service ID.
###############################################################################
sub setServiceInfo($$) {
    my ($serviceInfoFile,$currentDistro) = @_;
    my @serviceInfo = ();

    if(open(SERVICEINFO, "<$serviceInfoFile" )) {

	@serviceInfo = <SERVICEINFO>;

	close(SERVICEINFO);

    }
    else {
	print STDERR "$err Unable to find service, service type, and process information\n" .
	             "$spc for '$distro'.\n" .
	             "$spc Contact the Bastille support list for details.\n";
	exit(1);
    }


    # The following loop, parses the entire (YOUR OS).service file
    # to provide service information for YOUR OS.
    # The files format is as follows:
    # serviceID,servType,('service' 'configuration' 'list'),('process' 'list')[,DISTROS]*
    # if distros are not present then the service is assumed to be
    # relevant the the current distro


#
# More specifically, this file's format for rc-based daemons is:
#
# script_name,rc,(rc-config-file rc-config-file ...),(rc-variable1 rc-variable2 ...),('program_name1 program_name2 ...')
#
# ...where script_name is a file in /etc/init.d/ and
# ...program_nameN is a program launced by the script.
#
# This file's format for inet-based daemons is:
#
# identifier, inet, line name/file name, program name
#
# label,inet,(port1 port2 ...),(daemon1 daemon2 ...)
#
# ...where label is arbitrary, portN is one of the ports
# ...this one listens on, and daemonN is a program launched
# ...in response to a connection on a port.

    foreach my $service (@serviceInfo) {
	# This file accepts simple whole line comments perl style
	if($service !~ /^\s+\#|^\s+$/) {
	    chomp $service;
	    my ($serviceID,$servType,$strConfigList,$strServiceList,
		$strProcessList,@distroList) = split /\s*,\s*/, $service;
            
            sub MakeArrayFromString($){
                my $entryString = $_[0];
                my @destArray = ();
                if ($entryString =~ /\'\S+\'/) { #Make sure we have something to extract before we try
                    @destArray = split /\'\s+\'/, $entryString;
                    $destArray[0] =~ s/^\(\'(.+)$/$1/; # Remove leading quotation mark
                    $destArray[$#destArray] =~ s/^(.*)\'\)$/$1/; #Remove trailing quotation mark
                }
                return @destArray;
            }

	    # produce a list of configuration files from the files
	    # format ('configuration' 'files')
	    my @configList = MakeArrayFromString($strConfigList);

	    # produce a list of service configurables from the files
	    # format ('service' 'configurable')
	    my @serviceList = MakeArrayFromString($strServiceList);

	    # produce a list of process names from the files format
	    # ('my' 'process' 'list')
	    my @processList = MakeArrayFromString($strProcessList);

	    # if distros were not specified then accept the service information
	    if($#distroList == -1) {
		@{$GLOBAL_SERVICE{$serviceID}} = @serviceList;
		$GLOBAL_SERVTYPE{$serviceID} = $servType;
		@{$GLOBAL_PROCESS{$serviceID}} = @processList;
                @{$GLOBAL_RC_CONFIG{$serviceID}} = @configList;
	    }
	    else {
		# only if the current distro matches one of the listed distros
		# include the service information.
		foreach my $distro (@distroList) {
		    if($currentDistro =~ /$distro/) {
			@{$GLOBAL_SERVICE{$serviceID}} = @serviceList;
			$GLOBAL_SERVTYPE{$serviceID} = $servType;
			@{$GLOBAL_PROCESS{$serviceID}} = @processList;
                        @{$GLOBAL_RC_CONFIG{$serviceID}} = @configList;
		    }
		}
	    }
	}
    }
}



###############################################################################
#  getFileAndServiceInfo($distro,$actualDistro)
#
#  This subrountine, given distribution information, will import system file
#  and service information into the GLOBA_* hashes.
#
#  NOTE: $distro and $actualDistro will only differ when the --os switch is
#        used to generate a configuration file for an arbitrary operating
#        system.
#
###############################################################################
sub getFileAndServiceInfo($$) {

    my ($distro,$actualDistro) = @_;

    # defines the path to the OS map information for any supported OS type.
    # OS map information is used to determine file locations for a given
    # distribution.
    my %oSInfoPath = (
		       "LINUX" => "/usr/share/Bastille/OSMap/",
		       "HP-UX" => "/etc/opt/sec_mgmt/bastille/OSMap/",
		       "OSX" => "/usr/share/Bastille/OSMap/"
		       );

    # returns the OS, LINUX,  HP-UX, or OSX, associated with this
    # distribution
    my $actualOS = &getOSType($actualDistro);
    my $oS = &getOSType($distro);

    if(defined $actualOS && defined $oS) {
	my $bastilleInfoFile = $oSInfoPath{$actualOS} . "${actualOS}.bastille";
	my $systemInfoFile =  $oSInfoPath{$actualOS} . "${oS}.system";
	my $serviceInfoFile = $oSInfoPath{$actualOS} . "${oS}.service";

	if(-f $bastilleInfoFile) {
	    &setFileLocations($bastilleInfoFile,$actualDistro);
	}
	else {
	    print STDERR "$err Unable to find bastille file information.\n" .
		         "$spc $bastilleInfoFile does not exist on the system";
	    exit(1);
	}

	if(-f $systemInfoFile) {
	    &setFileLocations($systemInfoFile,$distro);
	}
	else {
	    print STDERR "$err Unable to find system file information.\n" .
		         "$spc $systemInfoFile does not exist on the system";
	    exit(1);
	}
	# Service info File is optional
	if(-f $serviceInfoFile) {
	    &setServiceInfo($serviceInfoFile,$distro);
	}
    }
    else {
	print STDERR "$err Unable to determine operating system type\n" .
	             "$spc for $actualDistro or $distro\n";
	exit(1);
    }

}


# returns the Operating System type associated with the specified
# distribution.
sub getOSType($) {

    my $distro = $_[0];

    my %supportedOSHash = &getSupportedOSHash;
    foreach my $oSType (keys %supportedOSHash) {
	foreach my $oSDistro (@{$supportedOSHash{$oSType}}) {
	    if($distro eq $oSDistro) {
		return $oSType;
	    }
	}
    }

    return undef;

}


# Test subroutine used to debug file location info for new Distributions as
# they are ported.
sub dumpFileInfo {
    print "Dumping File Information\n";
    foreach my $hashref (\%GLOBAL_BIN,\%GLOBAL_DIR,\%GLOBAL_FILE,\%GLOBAL_BFILE,\%GLOBAL_BDIR) {
	foreach my $id (keys %{$hashref}) {
	    print "$id: ${$hashref}{$id}\n";
	}
	print "-----------------------\n\n";
    }
}

# Test subroutine used to debug service info for new Distributions as
# they are ported.
sub dumpServiceInfo {
    print "Dumping Service Information\n";
    foreach my $serviceId (keys %GLOBAL_SERVICE) {
	print "$serviceId:\n";
	print "Type - $GLOBAL_SERVTYPE{$serviceId}\n";
	print "Service List:\n";
	foreach my $service (@{$GLOBAL_SERVICE{$serviceId}}) {
	    print "$service ";
	}
	print "\nProcess List:\n";
	foreach my $process (@{$GLOBAL_PROCESS{$serviceId}}) {
	    print "$process ";
	}
	print "\n----------------------\n";
    }
}


###########################################################################
#
# &ConfigureForDistro configures the API for a given distribution.  This
# includes setting global variables that tell the Bastille API about
# given binaries and directories.
#
# WARNING: If a distro is not covered here, Bastille may not be 100%
#          compatible with it, though 1.1 is written to be much smarter
#          about unknown distros...
#
###########################################################################
sub ConfigureForDistro {

    my $retval=1;

    # checking to see if the os version given is in fact supported
    my $distro = &GetDistro;

    # checking to see if the actual os version is in fact supported
    my $actualDistro = &getActualDistro;
    $ENV{'LOCALE'}=''; # So that test cases checking for english results work ok.
    if ((! &is_OS_supported($distro)) or (! &is_OS_supported($actualDistro))  ) {
	# if either is not supported then print out a list of supported versions
	if (! &is_OS_supported($distro)) {
	    print STDERR "$err '$distro' is not a supported operating system.\n";
	}
	else {
	    print STDERR "$err Bastille is unable to operate correctly on this\n";
	    print STDERR "$spc $distro operating system.\n";
	}
	my %supportedOSHash = &getSupportedOSHash;
	print STDERR "$spc Valid operating system versions are as follows:\n";

	foreach my $oSType (keys %supportedOSHash) {

	    print STDERR "$spc $oSType:\n$spc ";

	    my $os_number = 1;
	    foreach my $os (@{$supportedOSHash{$oSType}}) {
		print STDERR "'$os' ";
		if ($os_number == 5){
		    print STDERR "\n$spc ";
		    $os_number = 1;
		}
		else {
		    $os_number++;
		}

	    }
	    print STDERR "\n";
	}

	print "\n" . $GLOBAL_ERROR{"usage"};
	exit(1);
    }

    # First, let's make sure that we do not create any files or
    # directories with more permissive permissions than we
    # intend via setting the Perl umask
    umask(077);

    &getFileAndServiceInfo($distro,$actualDistro);

#    &dumpFileInfo;  # great for debuging file location issues
#    &dumpServiceInfo; # great for debuging service information issues

   # OS dependent error messages (after configuring file locations)
    my $nodisclaim_file = &getGlobal('BFILE', "nodisclaimer");

    $GLOBAL_ERROR{"disclaimer"}="$err Unable to touch $nodisclaim_file:" .
	    "$spc You must use Bastille\'s -n flag (for example:\n" .
	    "$spc bastille -f -n) or \'touch $nodisclaim_file \'\n";

    return $retval;
}


###########################################################################
###########################################################################
#                                                                         #
# The B_<perl_function> file utilities are replacements for their Perl    #
# counterparts.  These replacements log their actions and their errors,   #
# but are very similar to said counterparts.                              #
#                                                                         #
###########################################################################
###########################################################################


###########################################################################
# B_open is used for opening a file for reading.  B_open_plus is the preferred
# function for writing, since it saves a backup copy of the file for
# later restoration.
#
# B_open opens the given file handle, associated with the given filename
# and logs appropriately.
#
###########################################################################

sub B_open {
   my $retval=1;
   my ($handle,$filename)=@_;

   unless ($GLOBAL_LOGONLY) {
       $retval = open $handle,$filename;
   }

   ($handle) = "$_[0]" =~ /[^:]+::[^:]+::([^:]+)/;
   &B_log("ACTION","open $handle,\"$filename\";\n");
   unless ($retval) {
      &B_log("ERROR","open $handle, $filename failed...\n");
   }

   return $retval;
}

###########################################################################
# B_open_plus is the v1.1 open command.
#
# &B_open_plus($handle_file,$handle_original,$file) opens the file $file
# for reading and opens the file ${file}.bastille for writing.  It is the
# counterpart to B_close_plus, which will move the original file to
# $GLOBAL_BDIR{"backup"} and will place the new file ${file}.bastille in its
# place.
#
# &B_open_plus makes the appropriate log entries in the action and error
# logs.
###########################################################################

sub B_open_plus {

    my ($handle_file,$handle_original,$file)=@_;
    my $retval=1;
    my $return_file=1;
    my $return_old=1;

    my $original_file = $file;

    # Open the original file and open a copy for writing.
    unless ($GLOBAL_LOGONLY) {
	# if the temporary filename already exists then the open operation will fail.
        if ( $file eq "" ){
            &B_log("ERROR","Internal Error - Attempt Made to Open Blank Filename");
            $return_old=0;
	    $return_file=0;
            return 0; #False
        } elsif (-e "${file}.bastille") {
            &B_log("ERROR","Unable to open $file as the swap file ".
                   "${file}.bastille\" already exists.  Rename the swap ".
                   "file to allow Bastille to make desired file modifications.");
	    $return_old=0;
	    $return_file=0;
	}
	else {
	    $return_old = open $handle_original,"$file";
	    $return_file = open $handle_file,("> $file.bastille");
	}
    }

    # Error handling/logging here...
    #&B_log("ACTION","# Modifying file $original_file via temporary file $original_file.bastille\n");
    unless ($return_file) {
	$retval=0;
	&B_log("ERROR","open file: \"$original_file.bastille\" failed...\n");
    }
    unless ($return_old) {
	$retval=0;
	&B_log("ERROR","open file: \"$original_file\" failed.\n");
    }

    return $retval;

}

###########################################################################
# B_close was the v1.0 close command.  It is still used in places in the
# code.
# However the use of B _close_plus, which implements a new, smarter,
# backup scheme is preferred.
#
# B_close closes the given file handle, associated with the given filename
# and logs appropriately.
###########################################################################


sub B_close {
   my $retval=1;

   unless ($GLOBAL_LOGONLY) {
       $retval = close $_[0];
   }

   &B_log("ACTION", "close $_[0];\n");
   unless ($retval) {
      &B_log("ERROR", "close $_[0] failed...\n");
   }

   return $retval;
}


###########################################################################
# B_close_plus is the v1.1 close command.
#
# &B_close_plus($handle_file,$handle_original,$file) closes the files
# $file and ${file}.bastille, backs up $file to $GLOBAL_BDIR{"backup"} and
# renames ${file}.bastille to $file.  This backup is made using the
# internal API function &B_backup_file.  Further, it sets the new file's
# permissions and uid/gid to the same as the old file.
#
# B_close_plus is the counterpart to B_open_plus, which opened $file and
# $file.bastille with the file handles $handle_original and $handle_file,
# respectively.
#
# &B_close_plus makes the appropriate log entries in the action and error
# logs.
###########################################################################

sub B_close_plus {
    my ($handle_file,$handle_original,$file)=@_;
    my ($mode,$uid,$gid);
    my @junk;

    my $original_file;

    my $retval=1;
    my $return_file=1;
    my $return_old=1;

    # Append the global prefix, but save the original for B_backup_file b/c
    # it appends the prefix on its own...

    $original_file=$file;

    #
    # Close the files and prepare for the rename
    #

    if (($file eq "") or (not(-e $file ))) {
        &B_log("ERROR","Internal Error, attempted to close a blank filename ".
               "or nonexistent file.");
        return 0; #False
    }

    unless ($GLOBAL_LOGONLY) {
	$return_file = close $handle_file;
	$return_old = close $handle_original;
    }

    # Error handling/logging here...
    #&B_log("ACTION","#Closing $original_file and backing up to " . &getGlobal('BDIR', "backup"));
    #&B_log("ACTION","/$original_file\n");

    unless ($return_file) {
	$retval=0;
	&B_log("ERROR","close $original_file failed...\n");
    }
    unless ($return_old) {
	$retval=0;
	&B_log("ERROR","close $original_file.bastille failed.\n");
    }

    #
    # If we've had no errors, backup the old file and put the new one
    # in its place, with the Right permissions.
    #

    unless ( ($retval == 0) or $GLOBAL_LOGONLY) {

	# Read the permissions/owners on the old file

	@junk=stat ($file);
	$mode=$junk[2];
	$uid=$junk[4];
	$gid=$junk[5];

	# Set the permissions/owners on the new file

	chmod $mode, "$file.bastille" or &B_log("ERROR","Not able to retain permissions on $original_file!!!\n");
	chown $uid, $gid, "$file.bastille" or &B_log("ERROR","Not able to retain owners on $original_file!!!\n");

	# Backup the old file and put a new one in place.

	&B_backup_file($original_file);
	rename "$file.bastille", $file or
        &B_log("ERROR","B_close_plus: not able to move $original_file.bastille to $original_file\n");

        # We add the file to the GLOBAL_SUMS hash if it is not already present
	&B_set_sum($file);

    }

    return $retval;
}

###########################################################################
# &B_backup_file ($file) makes a backup copy of the file $file in
# &getGlobal('BDIR', "backup").  Note that this routine is intended for internal
# use only -- only Bastille API functions should call B_backup_file.
#
###########################################################################

sub B_backup_file {

    my $file=$_[0];
    my $complain = 1;
    my $original_file = $file;

    my $backup_dir = &getGlobal('BDIR', "backup");
    my $backup_file = $backup_dir . $original_file;

    my $retval=1;

    # First, separate the file into the directory and the relative filename

    my $directory ="";
    if ($file =~ /^(.*)\/([^\/]+)$/) {
	#$relative_file=$2;
	$directory = $1;
    } else {
        $directory=cwd;
    }

    # Now, if the directory does not exist, create it.
    # Later:
    #   Try to set the same permissions on the patch directory that the
    #   original had...?

    unless ( -d ($backup_dir . $directory) ) {
	mkpath(( $backup_dir . $directory),0,0700);

    }

    # Now we backup the file.  If there is already a backup file there,
    # we will leave it alone, since it exists from a previous run and
    # should be the _original_ (possibly user-modified) distro's version
    # of the file.

    if ( -e $file ) {

	unless ( -e $backup_file ) {
	    my $command=&getGlobal("BIN","cp");
            &B_Backtick("$command -p $file $backup_file");
	    &B_revert_log (&getGlobal("BIN","mv"). " $backup_file $file");
	}

    } else {
	# The file we were trying to backup doesn't exist.

	$retval=0;
	# This is a non-fatal error, not worth complaining about
	$complain = 0;
	#&ErrorLog ("# Failed trying to backup file $file -- it doesn't exist!\n");
    }

    # Check to make sure that the file does exist in the backup location.

    unless ( -e $backup_file ) {
	$retval=0;
	if ( $complain == 1 ) {
	    &B_log("ERROR","Failed trying to backup $file -- the copy was not created.\n");
	}
    }

    return $retval;
}


###########################################################################
# &B_read_sums reads in the sum.csv file which contains information
#   about Bastille modified files. The file structure is as follows:
#
#     filename,filesize,cksum
#
#   It reads the information into the GLOBAL_SUM hash i.e.
#      $GLOBAL_SUM{$file}{sum} = $cksum
#      $GLOBAL_SUM{$file}{filesize} = $size
#   For the first run of Bastille on a given system this subroutine
#   is a no-op, and returns "undefined."
###########################################################################

sub B_read_sums {

    my $sumFile = &getGlobal('BFILE',"sum.csv");

    if ( -e $sumFile ) {

	open( SUM, "< $sumFile") or &B_log("ERROR","Unable to open $sumFile for read.\n$!\n");

	while( my $line = <SUM> ) {
	    chomp $line;
	    my ($file,$filesize,$sum,$flag) = split /,/, $line;
	    if(-e $file) {
		$GLOBAL_SUM{"$file"}{filesize} = $filesize;
		$GLOBAL_SUM{"$file"}{sum} = $sum;
	    }
	}

	close(SUM);
    } else {
        return undef;
    }
}


###########################################################################
# &B_write_sums writes out the sum.csv file which contains information
#   about Bastille modified files. The file structure is as follows:
#
#     filename,filesize,cksum
#
#   It writes the information from the GLOBAL_SUM hash i.e.
#
#      $file,$GLOBAL_SUM{$file}{sum},$GLOBAL_SUM{$file}{filesize}
#
#   This subroutine requires access to the GLOBAL_SUM hash.
###########################################################################

sub B_write_sums {

    my $sumFile = &getGlobal('BFILE',"sum.csv");

    if ( %GLOBAL_SUM ) {

	open( SUM, "> $sumFile") or &B_log("ERROR","Unable to open $sumFile for write.\n$!\n");

	for my $file (sort keys %GLOBAL_SUM) {
	    if( -e $file) {
		print SUM "$file,$GLOBAL_SUM{\"$file\"}{filesize},$GLOBAL_SUM{\"$file\"}{sum}\n";
	    }
	}

	close(SUM);
    }

}


###########################################################################
# &B_check_sum($file) compares the stored cksum and filesize of the given
#   file compared to the current cksum and filesize respectively.
#   This subroutine also keeps the state of the sum check by setting the
#   checked flag which tells the subroutine that on this run this file
#   has already been checked.
#
#     $GLOBAL_SUM{$file}{checked} = 1;
#
#   This subroutine requires access to the GLOBAL_SUM hash.
#
#  Returns 1 if sum checks out and 0 if not
###########################################################################

sub B_check_sum($) {
    my $file = $_[0];
    my $cksum = &getGlobal('BIN',"cksum");

    if (not(%GLOBAL_SUM)) {
        &B_read_sums;
    }

    if(-e $file) {
	my ($sum,$size,$ckfile) = split(/\s+/, `$cksum $file`);
        my $commandRetVal = ($? >> 8);  # find the command's return value

	if($commandRetVal != 0) {
	    &B_log("ERROR","$cksum reported the following error:\n$!\n");
            return 0;
	} else {
            if ( exists $GLOBAL_SUM{$file} ) {
                # if the file size or file sum differ from those recorded.
                if (( $GLOBAL_SUM{$file}{filesize} == $size) and
                    ($GLOBAL_SUM{$file}{sum} == $sum )) {
                    return 1; #True, since saved state matches up, all is well.
                } else {
                    return 0; #False, since saved state doesn't match
                }
            } else {
                &B_log("ERROR","File: $file does not exist in sums database.");
                return 0;
            }
        }
    } else {
        &B_log("ERROR","The file: $file does not exist for comparison in B_check_sum.");
        return 0;
    }
}

# Don't think we need this anymore as function now check_sums returns
# results directly
#sub isSumDifferent($) {
#    my $file = $_[0];
#    if(exists $GLOBAL_SUM{$file}) {
#	return $GLOBAL_SUM{$file}{flag}
#    }
#}

sub listModifiedFiles {
    my @listModifiedFiles=sort keys %GLOBAL_SUM;
    return @listModifiedFiles;
}

###########################################################################
# &B_isFileinSumDB($file) checks to see if a given file's sum was saved.
#
#     $GLOBAL_SUM{$file}{filesize} = $size;
#     $GLOBAL_SUM{$file}{sum} = $cksum;
#
#   This subroutine requires access to the GLOBAL_SUM hash.
###########################################################################

sub B_isFileinSumDB($) {
    my $file = $_[0];

    if (not(%GLOBAL_SUM)) {
        &B_log("DEBUG","Reading in DB from B_isFileinSumDB");
        &B_read_sums;
    }
    if (exists($GLOBAL_SUM{"$file"})){
        &B_log("DEBUG","$file is in sum database");
        return 1; #true
    } else {
        &B_log("DEBUG","$file is not in sum database");
        return 0; #false
    }
}

###########################################################################
# &B_set_sum($file) sets the current cksum and filesize of the given
#   file into the GLOBAL_SUM hash.
#
#     $GLOBAL_SUM{$file}{filesize} = $size;
#     $GLOBAL_SUM{$file}{sum} = $cksum;
#
#   This subroutine requires access to the GLOBAL_SUM hash.
###########################################################################

sub B_set_sum($) {

    my $file = $_[0];
    my $cksum = &getGlobal('BIN',"cksum");
    if( -e $file) {

	my ($sum,$size,$ckfile) = split(/\s+/, `$cksum $file`);
        my $commandRetVal = ($? >> 8);  # find the command's return value

	if($commandRetVal != 0) {

	    &B_log("ERROR","$cksum reported the following error:\n$!\n");

	}
	else {

	    # new file size and sum are added to the hash
	    $GLOBAL_SUM{$file}{filesize} = $size;
	    $GLOBAL_SUM{$file}{sum} = $sum;
	    &B_write_sums;

	}
    } else {
        &B_log("ERROR","Can not save chksum for file: $file since it does not exist");
    }
}


###########################################################################
#
# &B_delete_file ($file)  deletes the file $file and makes a backup to
# the backup directory.
#
##########################################################################


sub B_delete_file($) { #Currently Linux only (TMPDIR)
    #consideration: should create clear_sum routine if this is ever used to remove
    #               A Bastille-generated file.

    #
    # This API routine deletes the named file, backing it up first to the
    # backup directory.
    #

    my $filename=shift @_;
    my $retval=1;

    # We have to append the prefix ourselves since we don't use B_open_plus

    my $original_filename=$filename;

    &B_log("ACTION","Deleting (and backing-up) file $original_filename\n");
    &B_log("ACTION","rm $original_filename\n");

    unless ($filename) {
	&B_log("ERROR","B_delete_file called with no arguments!\n");
    }

    unless ($GLOBAL_LOGONLY) {
	if ( B_backup_file($original_filename) ) {
	    unless ( unlink $filename ) {
		&B_log("ERROR","Couldn't unlink file $original_filename");
		$retval=0;
	    }
	}
	else {
	    $retval=0;
	    &B_log("ERROR","B_delete_file did not delete $original_filename since it could not back it up\n");
	}
    }

    $retval;

}


###########################################################################
# &B_create_file ($file) creates the file $file, if it doesn't already
# exist.
# It will set a default mode of 0700 and a default uid/gid or 0/0.
#
# &B_create_file, to support Bastille's revert functionality, writes an
# rm $file command to the end of the file &getGlobal('BFILE', "created-files").
#
##########################################################################


sub B_create_file($) {

    my $file = $_[0];
    my $retval=1;

    # We have to create the file ourselves since we don't use B_open_plus

    my $original_file = $file;

    if ($file eq ""){
        &B_log("ERROR","Internal Error, attempt made to create blank filename");
        return 0; #False
    }

    unless ( -e $file ) {

	unless ($GLOBAL_LOGONLY) {

	    # find the directory in which the file is to reside.
	    my $dirName = dirname($file);
	    # if the directory does not exist then
	    if(! -d $dirName) {
		# create it.
		mkpath ($dirName,0,0700);
	    }

	    $retval=open CREATE_FILE,">$file";

	    if ($retval) {
		close CREATE_FILE;
		chmod 0700,$file;
		# Make the revert functionality
		&B_revert_log( &getGlobal('BIN','rm') . " $original_file \n");
	    } else {
		&B_log("ERROR","Couldn't create file $original_file even though " .
			  "it didn't already exist!\n");
	    }
	}
	&B_log("ACTION","Created file $original_file\n");
    } else {
	&B_log("DEBUG","Didn't create file $original_file since it already existed.\n");
	$retval=0;
    }

    $retval;
}


###########################################################################
# &B_create_dir ($dir) creates the directory $dir, if it doesn't already
# exist.
# It will set a default mode of 0700 and a default uid/gid or 0/0.
#
##########################################################################


sub B_create_dir($) {

    my $dir = $_[0];
    my $retval=1;

    # We have to append the prefix ourselves since we don't use B_open_plus

    my $original_dir=$dir;

    unless ( -d $dir ) {
	unless ($GLOBAL_LOGONLY) {
	    $retval=mkdir $dir,0700;

	    if ($retval) {
		# Make the revert functionality
		&B_revert_log (&getGlobal('BIN','rmdir') . " $original_dir\n");
	    }
	    else {
		&B_log("ERROR","Couldn't create dir $original_dir even though it didn't already exist!");
	    }

	}
	&B_log("ACTION","Created directory $original_dir\n");
    }
    else {
	&B_log("ACTION","Didn't create directory $original_dir since it already existed.\n");
	$retval=0;
    }

    $retval;
}



###########################################################################
# &B_symlink ($original_file,$new_symlink) creates a symbolic link from
# $original_file to $new_symlink.
#
# &B_symlink respects $GLOBAL_LOGONLY.  It supports
# the revert functionality that you've come to know and love by adding every
# symbolic link it creates to &getGlobal('BFILE', "created-symlinks"), currently set to:
#
#         /root/Bastille/revert/revert-created-symlinks
#
# The revert script, if it works like I think it should, will run this file,
# which should be a script or rm's...
#
##########################################################################

sub B_symlink($$) {
    my ($source_file,$new_symlink)=@_;
    my $retval=1;
    my $original_source = $source_file;
    my $original_symlink = $new_symlink;

    unless ($GLOBAL_LOGONLY) {
	$retval=symlink $source_file,$new_symlink;
	if ($retval) {
	    &B_revert_log (&getGlobal('BIN',"rm") .  " $original_symlink\n");
	}
    }

    &B_log("ACTION", "Created a symbolic link called $original_symlink from $original_source\n");
    &B_log("ACTION", "symlink \"$original_source\",\"$original_symlink\";\n");
    unless ($retval) {
	&B_log("ERROR","Couldn't symlink $original_symlink -> $original_source\n");
    }

    $retval;

}


sub B_cp($$) {

    my ($source,$target)=@_;
    my $retval=0;

    my $had_to_backup_target=0;

    use File::Copy;

    my $original_source=$source;
    my $original_target=$target;

    if( -e $target and -f $target ) {
	&B_backup_file($original_target);
	&B_log("ACTION","About to copy $original_source to $original_target -- had to backup target\n");
	$had_to_backup_target=1;
    }

    $retval=copy($source,$target);
    if ($retval) {
	&B_log("ACTION","cp $original_source $original_target\n");

	#
	# We want to add a line to the &getGlobal('BFILE', "created-files") so that the
	# file we just put at $original_target gets deleted.
	#
	&B_revert_log(&getGlobal('BIN',"rm") . " $original_target\n");
    } else {
	&B_log("ERROR","Failed to copy $original_source to $original_target\n");
    }
    # We add the file to the GLOBAL_SUMS hash if it is not already present
    &B_set_sum($target);
    $retval;
}



############################################################################
# &B_place puts a file in place, using Perl's File::cp.  This file is taken
# from &getGlobal('BDIR', "share") and is used to place a file that came with
# Bastille.
#
# This should be DEPRECATED in favor of &B_cp, since the only reason it exists
# is because of GLOBAL_PREFIX, which has been broken for quite some time.
# Otherwise, the two routines are identical.
#
# It respects $GLOBAL_LOGONLY.
# If $target is an already-existing file, it is backed up.
#
# revert either appends another "rm $target" to &getGlobal('BFILE', "revert-actions")  or
# backs up the file that _was_ there into the &getGlobal('BDIR', "backup"),
# appending a "mv" to revert-actions to put it back.
#
############################################################################

sub B_place { # Only Linux references left (Firewall / TMPDIR)

    my ($source,$target)=@_;
    my $retval=0;

    my $had_to_backup_target=0;

    use File::Copy;

    my $original_source=$source;
    $source  = &getGlobal('BDIR', "share") . $source;
    my $original_target=$target;

    if ( -e $target and -f $target ) {
	&B_backup_file($original_target);
	&B_log("ACTION","About to copy $original_source to $original_target -- had to backup target\n");
	$had_to_backup_target=1;
    }
    $retval=copy($source,$target);
    if ($retval) {
	&B_log("ACTION","placed file $original_source  as  $original_target\n");
	#
	# We want to add a line to the &getGlobal('BFILE', "created-files") so that the
	# file we just put at $original_target gets deleted.
	&B_revert_log(&getGlobal('BIN',"rm") . " $original_target\n");
    } else {
	&B_log("ERROR","Failed to place $original_source as $original_target\n");
    }

    # We add the file to the GLOBAL_SUMS hash if it is not already present
    &B_set_sum($target);

    $retval;
}





#############################################################################
#############################################################################
#############################################################################

###########################################################################
# &B_mknod ($file) creates the node $file, if it doesn't already
# exist.  It uses the prefix and suffix, like this:
#
#            mknod $prefix $file $suffix
#
# This is just a wrapper to the mknod program, which tries to introduce
# revert functionality, by writing    rm $file     to the end of the
# file &getGlobal('BFILE', "created-files").
#
##########################################################################


sub B_mknod($$$) {

    my ($prefix,$file,$suffix) = @_;
    my $retval=1;

    # We have to create the filename ourselves since we don't use B_open_plus

    my $original_file = $file;

    unless ( -e $file ) {
	my $command = &getGlobal("BIN","mknod") . " $prefix $file $suffix";

	if ( system($command) == 0) {
	    # Since system will return 0 on success, invert the error code
	    $retval=1;
	}
	else {
	    $retval=0;
	}

	if ($retval) {

	    # Make the revert functionality
	    &B_revert_log(&getGlobal('BIN',"rm") . " $original_file\n");
	} else {
	    &B_log("ERROR","Couldn't mknod $prefix $original_file $suffix even though it didn't already exist!\n");
	}


	&B_log("ACTION","mknod $prefix $original_file $suffix\n");
    }
    else {
	&B_log("ACTION","Didn't mknod $prefix $original_file $suffix since $original_file already existed.\n");
	$retval=0;
    }

    $retval;
}

###########################################################################
# &B_revert_log("reverse_command") prepends a command to a shell script.  This
# shell script is intended to be run by bastille -r to reverse the changes that
# Bastille made, returning the files which Bastille changed to their original
# state.
###########################################################################

sub B_revert_log($) {

    my $revert_command = $_[0];
    my $revert_actions = &getGlobal('BFILE', "revert-actions");
    my $revertdir= &getGlobal('BDIR', "revert");
    my @lines;


    if (! (-e $revert_actions)) {
        mkpath($revertdir); #if this doesn't work next line catches
	if (open REVERT_ACTIONS,">" . $revert_actions){ # create revert file
	    close REVERT_ACTIONS; # chown to root, rwx------
	    chmod 0700,$revert_actions;
	    chown 0,0,$revert_actions;
	}
	else {
	    &B_log("FATAL","Can not create revert-actions file: $revert_actions.\n" .
		       "         Unable to add the following command to the revert\n" .
		       "         actions script: $revert_command\n");
	}

    }

    &B_open_plus (*REVERT_NEW, *REVERT_OLD, $revert_actions);

    while (my $line=<REVERT_OLD>) { #copy file into @lines
	push (@lines,$line);
    }
    print REVERT_NEW $revert_command .  "\n";  #make the revert command first in the new file
    while (my $line = shift @lines) { #write the rest of the lines of the file
	print REVERT_NEW $line;
    }
    close REVERT_OLD;
    close REVERT_NEW;
    if (rename "${revert_actions}.bastille", $revert_actions) { #replace the old file with the new file we
	chmod 0700,$revert_actions;                # just made / mirrors B_close_plus logic
	chown 0,0,$revert_actions;
    } else {
	&B_log("ERROR","B_revert_log: not able to move ${revert_actions}.bastille to ${revert_actions}!!! $!) !!!\n");
    }
}


###########################################################################
# &getGlobalConfig($$)
#
# returns the requested GLOBAL_CONFIG hash value, ignoring the error
# if the value does not exist (because every module uses this to find
# out if the question was answered "Y")
###########################################################################
sub getGlobalConfig ($$) {
  my $module = $_[0];
  my $key = $_[1];
  if (exists $GLOBAL_CONFIG{$module}{$key}) {
    my $answer=$GLOBAL_CONFIG{$module}{$key};
    &B_log("ACTION","Answer to question $module.$key is \"$answer\".\n");
    return $answer;
  } else {
    &B_log("ACTION","Answer to question $module.$key is undefined.");
    return undef;
  }
}

###########################################################################
# &getGlobal($$)
#
# returns the requested GLOBAL_* hash value, and logs an error
# if the variable does not exist.
###########################################################################
sub getGlobal ($$) {
  my $type = uc($_[0]);
  my $key = $_[1];

  # define a mapping from the first argument to the proper hash
  my %map = ("BIN"   => \%GLOBAL_BIN,
             "FILE"  => \%GLOBAL_FILE,
             "BFILE" => \%GLOBAL_BFILE,
             "DIR"   => \%GLOBAL_DIR,
             "BDIR"  => \%GLOBAL_BDIR,
	     "ERROR" => \%GLOBAL_ERROR,
	     "SERVICE" => \%GLOBAL_SERVICE,
	     "SERVTYPE" => \%GLOBAL_SERVTYPE,
	     "PROCESS" => \%GLOBAL_PROCESS,
             "RCCONFIG" => \%GLOBAL_RC_CONFIG
            );

  # check to see if the desired key is in the desired hash
  if (exists $map{$type}->{$key}) {
    # get the value from the right hash with the key
    return $map{$type}->{$key};
  } else {
    # i.e. Bastille tried to use $GLOBAL_BIN{'cp'} but it does not exist.
    # Note that we can't use B_log, since it uses getGlobal ... recursive before
    # configureForDistro is run.
    print STDERR "ERROR:   Bastille tried to use \$GLOBAL_${type}\{\'$key\'} but it does not exist.\n";
    return undef;
  }
}

###########################################################################
# &getGlobal($$)
#
# sets the requested GLOBAL_* hash value
###########################################################################
sub setGlobal ($$$) {
  my $type = uc($_[0]);
  my $key = $_[1];
  my $input_value = $_[2];

  # define a mapping from the first argument to the proper hash
  my %map = ("BIN"   => \%GLOBAL_BIN,
             "FILE"  => \%GLOBAL_FILE,
             "BFILE" => \%GLOBAL_BFILE,
             "DIR"   => \%GLOBAL_DIR,
             "BDIR"  => \%GLOBAL_BDIR,
	     "ERROR" => \%GLOBAL_ERROR,
	     "SERVICE" => \%GLOBAL_SERVICE,
	     "SERVTYPE" => \%GLOBAL_SERVTYPE,
	     "PROCESS" => \%GLOBAL_PROCESS,
            );

  if ($map{$type}->{$key} = $input_value) {
    return 1;
  } else {
    &B_log('ERROR','Internal Error, Unable to set global config value:' . $type . ", " .$key);
    return 0;
  }
}


###########################################################################
# &showDisclaimer:
# Print the disclaimer and wait for 2 minutes for acceptance
# Do NOT do so if any of the following conditions hold
# 1. the -n option was used
# 2. the file ~/.bastille_disclaimer exists
###########################################################################

sub showDisclaimer($) {

    my $nodisclaim = $_[0];
    my $nodisclaim_file = &getGlobal('BFILE', "nodisclaimer");
    my $response;
    my $WAIT_TIME = 300; # we'll wait for 5 minutes
    my $developersAnd;
    my $developersOr;
    if ($GLOBAL_OS =~ "^HP-UX") {
	$developersAnd ="HP AND ITS";
	$developersOr ="HP OR ITS";
    }else{
	$developersAnd ="JAY BEALE, THE BASTILLE DEVELOPERS, AND THEIR";
	$developersOr ="JAY BEALE, THE BASTILLE DEVELOPERS, OR THEIR";
    }
    my $DISCLAIMER =
	"\n" .
        "Copyright (C) 1999-2006 Jay Beale\n" .
        "Copyright (C) 1999-2001 Peter Watkins\n" .
        "Copyright (C) 2000 Paul L. Allen\n" .
        "Copyright (C) 2001-2007 Hewlett-Packard Development Company, L.P.\n" .
        "Bastille is free software; you are welcome to redistribute it under\n" .
        "certain conditions.  See the \'COPYING\' file in your distribution for terms.\n\n" .
	"DISCLAIMER.  Use of Bastille can help optimize system security, but does not\n" .
	"guarantee system security. Information about security obtained through use of\n" .
	"Bastille is provided on an AS-IS basis only and is subject to change without\n" .
	"notice. Customer acknowledges they are responsible for their system\'s security.\n" .
	"TO THE EXTENT ALLOWED BY LOCAL LAW, Bastille (\"SOFTWARE\") IS PROVIDED TO YOU \n" .
	"\"AS IS\" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, WHETHER ORAL OR WRITTEN,\n" .
	"EXPRESS OR IMPLIED.  $developersAnd SUPPLIERS\n" .
	"DISCLAIM ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE \n" .
	"IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.\n" .
	"Some countries, states and provinces do not allow exclusions of implied\n" .
	"warranties or conditions, so the above exclusion may not apply to you. You may\n" .
	"have other rights that vary from country to country, state to state, or province\n" .
	"to province.  EXCEPT TO THE EXTENT PROHIBITED BY LOCAL LAW, IN NO EVENT WILL\n" .
	"$developersOr SUBSIDIARIES, AFFILIATES OR\n" .
	"SUPPLIERS BE LIABLE FOR DIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL OR OTHER\n" .
	"DAMAGES (INCLUDING LOST PROFIT, LOST DATA, OR DOWNTIME COSTS), ARISING OUT OF\n" .
	"THE USE, INABILITY TO USE, OR THE RESULTS OF USE OF THE SOFTWARE, WHETHER BASED\n" .
	"IN WARRANTY, CONTRACT, TORT OR OTHER LEGAL THEORY, AND WHETHER OR NOT ADVISED\n" .
	"OF THE POSSIBILITY OF SUCH DAMAGES. Your use of the Software is entirely at your\n" .
	"own risk. Should the Software prove defective, you assume the entire cost of all\n" .
	"service, repair or correction. Some countries, states and provinces do not allow\n" .
	"the exclusion or limitation of liability for incidental or consequential \n" .
	"damages, so the above limitation may not apply to you.  This notice will only \n".
        "display on the first run on a given system.\n".
        "To suppress the disclaimer on other machines, use Bastille\'s -n flag (example: bastille -n).\n";


# If the user has specified not to show the disclaimer, or
# the .bastille_disclaimer file already exists, then return
    if( ( $nodisclaim ) || -e $nodisclaim_file ) { return 1; }

# otherwise, show the disclaimer
    print ($DISCLAIMER);

# there is a response
	my $touch = &getGlobal('BIN', "touch");
	my $retVal = system("$touch $nodisclaim_file");
	if( $retVal != 0 ) {
	    &ErrorLog ( &getGlobal('ERROR','disclaimer'));
	}
} # showDisclaimer




################################################################
# &systemCall
#Function used by exported methods B_Backtick and B_system
#to handle the mechanics of system calls.
# This function also manages error handling.
# Input: a system call
# Output: a list containing the status, sstdout and stderr
# of the the system call
#
################################################################
sub systemCall ($){
    no strict;
    local $command=$_[0];  # changed scoping so eval below can read it

    local $SIG{'ALRM'} = sub {  die "timeout" }; # This subroutine exits the "eval" below.  The program
    # can then move on to the next operation.  Used "local"
    # to avoid name space collision with disclaim alarm.
    local $WAIT_TIME=120; # Wait X seconds for system commands
    local $commandOutput = '';
    my $errOutput = '';
    eval{
        $errorFile = &getGlobal('BFILE','stderrfile');
        unlink($errorFile); #To make sure we don't mix output
	alarm($WAIT_TIME); # start a time-out for command to complete.  Some commands hang, and we want to
	                   # fail gracefully.  When we call "die" it exits this eval statement
	                   # with a value we use below
	$commandOutput = `$command 2> $errorFile`; # run the command and gather its output
	my $commandRetVal = ($? >> 8);  # find the commands return value
	if ($commandRetVal == 0) {
	    &B_log("ACTION","Executed Command: " . $command . "\n");
	    &B_log("ACTION","Command Output: " . $commandOutput . "\n");
	    die "success";
	} else {
	    die "failure";
	};
    };

    my $exitcode=$@;
    alarm(0);  # End of the timed operation

    my $cat = &getGlobal("BIN","cat");
    if ( -e $errorFile ) {
        $errOutput = `$cat $errorFile`;
    }

    if ($exitcode) {  # The eval command above will exit with one of the 3 values below
	if ($exitcode =~ /timeout/) {
	    &B_log("WARNING","No response received from $command after $WAIT_TIME seconds.\n" .
		   "Command Output: " . $commandOutput . "\n");
	    return (0,'','');
	} elsif ($exitcode =~ /success/) {
	    return (1,$commandOutput,$errOutput);
	} elsif ($exitcode =~ /failure/) {
	    return (0,$commandOutput,$errOutput);
	} else {
	    &B_log("FATAL","Unexpected return state from command execution: $command\n" .
		   "Command Output: " . $commandOutput . "\n");
	}
    }
}

#############################################
# Use this **only** for commands used that are
# intended to test system state and
# not make any system change.  Use this in place of the
# prior use of "backticks throughout Bastille
# Handles basic output redirection, but not for stdin
# Input: Command
# Output: Results
#############################################

sub B_Backtick($) {
    my $command=$_[0];
    my $combineOutput=0;
    my $stdoutRedir = "";
    my $stderrRedir = "";
    my $echo = &getGlobal('BIN','echo');

    if (($command =~ s/2>&1//) or
        (s/>&2//)){
        $combineOutput=1;
    }
    if ($command =~ s/>\s*([^>\s])+// ) {
        $stdoutRedir = $1;
    }
    if ($command =~ s/2>\s*([^>\s])+// ) {
        $stderrRedir = $1;
    }

    my ($ranFine, $stdout, $stderr) = &systemCall($command);
    if ($ranFine) {
        &B_log("DEBUG","Command: $command succeeded for test with output: $stdout , ".
               "and stderr: $stderr");
    } else {
        &B_log("DEBUG","Command: $command failed for test with output: $stdout , ".
               "and stderr: $stderr");
    }
    if ($combineOutput) {
        $stdout .= $stderr;
        $stderr = $stdout; #these should be the same
    }
    if ($stdoutRedir ne "") {
        system("$echo \'$stdout\' > $stdoutRedir");
    }
    if ($stderrRedir ne "") {
        system("$echo \'$stderr\' > $stderrRedir");
    }
    return $stdout;
}

####################################################################
#  &B_System($command,$revertcommand);
#    This function executes a command, then places the associated
#    revert command in revert file. It takes two parameters, the
#    command and the command that reverts that command.
#
#   uses ActionLog and ErrorLog for logging purposes.
###################################################################
sub B_System ($$) {
    my ($command,$revertcmd)=@_;

    my ($ranFine, $stdout, $stderr) = &systemCall($command);
    if ($ranFine) {
        &B_revert_log ("$revertcmd \n");
        if ($stderr ne '' ) {
                &B_log("ACTION",$command . "suceeded with STDERR: " .
                       $stderr . "\n");
        }
        return 1;
    } else {
        my $warningString = "Command Failed: " . $command . "\n" .
                            "Command Output: " . $stdout . "\n";
        if ($stderr ne '') {
            $warningString .= "Error message: " . $stderr;
        }
        &B_log("WARNING", $warningString);
        return 0;
    }
}


###########################################################################
# &isProcessRunning($procPattern);
#
# If called in scalar context this subroutine will return a 1 if the
# pattern specified can be matched against the process table.  It will
# return a 0 otherwise.
# If called in the list context this subroutine will return the list
# of processes which matched the pattern supplied
#
# scalar return values:
# 0:     pattern not in process table
# 1:     pattern is in process table
#
# list return values:
# proc lines from the process table if they are found
###########################################################################
sub isProcessRunning($) {

    my $procPattern= $_[0];
    my $ps = &getGlobal('BIN',"ps");

    my $isRunning=0;
    # process table.
    my @psTable = `$ps -elf`;
    # list of processes that match the $procPattern
    my @procList;
    foreach my $process (@psTable) {
        if($process =~ $procPattern) {
            $isRunning = 1;
            push @procList, $process . "\n";
        }
    }
    
    &B_log("DEBUG","$procPattern search yielded $isRunning\n\n");
    # if this subroutine was called in scalar context
    if( ! wantarray ) {
        return $isRunning;
    }

    return @procList;
}


###########################################################################
# &checkProcsForService($service);
#
# Checks if the given service is running by analyzing the process table.
# This is a helper function to checkServiceOnLinux and checkServiceOnHP
#
# Return values:
# SECURE_CANT_CHANGE() if the service is off
# INCONSISTENT() if the state of the service cannot be determined
#
# Mostly used in  "check service" direct-return context, but added option use.
# to ignore warning if a check for a service ... where a found service doesn't
# have direct security problems.
#
###########################################################################
sub checkProcsForService ($;$) {
  my $service=$_[0];
  my $ignore_warning=$_[1];

  my @psnames=@{ &getGlobal('PROCESS',$service)};

  my @processes;
  # inetd services don't have a separate process
  foreach my $psname (@psnames) {
    my @procList = &isProcessRunning($psname);
    if(@procList >= 0){
      splice @processes,$#processes+1,0,@procList;
    }
  }

  if($#processes >= 0){
    if ((defined($ignore_warning)) and ($ignore_warning eq "ignore_warning")) {
      &B_log("WARNING","The following processes were still running even though " .
           "the corresponding service appears to be turned off.  Bastille " .
           "question and action will be skipped.\n\n" .
           "@processes\n\n");
      # processes were still running, service is not off, but we don't know how
      # to configure it so we skip the question
    return INCONSISTENT();
    } else {
      return NOTSECURE_CAN_CHANGE(); # In the case we're ignoring the warning,
                                     # ie: checking to make *sure* a process
                                     # is running, the answer isn't inconsistent
    }
  } else {
    &B_log("DEBUG","$service is off.  Found no processes running on the system.");
    # no processes, so service is off
    return SECURE_CANT_CHANGE();
  }
  # Can't determine the state of the service by looking at the processes,
  # so return INCONSISTENT().
  return INCONSISTENT();
}

###########################################################################
# B_parse_fstab()
#
# Search the filesystem table for a specific mount point.
#
# scalar return value:
# The line form the table that matched the mount point, or the null string
# if no match was found.
#
# list return value:
# A list of parsed values from the line of the table that matched, with
# element [3] containing a reference to a hash of the mount options.  The
# keys are: acl, dev, exec, rw, suid, sync, or user.  The value of each key
# can be either 0 or 1.  To access the hash, use code similar to this:
# %HashResult = %{(&B_parse_fstab($MountPoint))[3]};
#
###########################################################################

sub B_parse_fstab($)
{
    my $name = shift;
    my $file = &getGlobal('FILE','fstab');
    my ($enable, $disable, $infile);
    my @lineopt;
    my $retline = "";
    my @retlist = ();

    unless (open FH, $file) {
	&B_log('ERROR',"B_parse_fstab couldn't open fstab file at path $file.\n");
	return 0;
    }
    while (<FH>) {
        s/\#.*//;
        next unless /\S/;
        @retlist = split;
        next unless $retlist[1] eq $name;
        $retline  .= $_;
        if (wantarray) {
            my $option = {		# initialize to defaults
            acl    =>  0,		# for ext2, etx3, reiserfs
            dev    =>  1,
            exec   =>  1,
            rw     =>  1,
            suid   =>  1,
            sync   =>  0,
            user   =>  0,
            };

            my @lineopt = split(',',$retlist[3]);
            foreach my $entry (@lineopt) {
                if ($entry eq 'acl') {
                    $option->{'acl'} = 1;
                }
                elsif ($entry eq 'nodev') {
                    $option->{'dev'} = 0;
                }
                elsif ($entry eq 'noexec') {
                    $option->{'exec'} = 0;
                }
                elsif ($entry eq 'ro') {
                    $option->{'rw'} = 0;
                }
                elsif ($entry eq 'nosuid') {
                    $option->{'suid'} = 0;
                }
                elsif ($entry eq 'sync') {
                    $option->{'sync'} = 1;
                }
                elsif ($entry eq 'user') {
                    $option->{'user'} = 1;
                }
            }
            $retlist[3]= $option;
        }
        last;
    }

    if (wantarray)
    {
        return @retlist;
    }
    else
    {
        return $retline;
    }

}


###########################################################################
# B_parse_mtab()
#
# This routine returns a hash of devices and their mount points from mtab,
# simply so you can get a list of mounted filesystems.
#
###########################################################################

sub B_parse_mtab
{
    my $mountpoints;
    open(MTAB,&getGlobal('FILE','mtab'));
    while(my $mtab_line = <MTAB>) {
        #test if it's a device
        if ($mtab_line =~ /^\//)
        {
           #parse out device and mount point
           $mtab_line =~ /^(\S+)\s+(\S+)/;
           $mountpoints->{$1} = $2;
        }
     }
     return $mountpoints;
}


###########################################################################
# B_is_rpm_up_to_date()
#
#
###########################################################################

sub B_is_rpm_up_to_date(@)
{
    my($nameB,$verB,$relB,$epochB) = @_;
    my $installedpkg = $nameB;

    if ($epochB =~ /(none)/) {
	$epochB = 0;
    }

    my $rpmA   = `rpm -q --qf '%{VERSION}-%{RELEASE}-%{EPOCH}\n' $installedpkg`;
    my $nameA  = $nameB;
    my ($verA,$relA,$epochA);

    my $retval;

    # First, if the RPM isn't installed, let's handle that.
    if ($rpmA =~ /is not installed/) {
	$retval = -1;
	return $retval;
    }
    else {
	# Next, let's try to parse the EVR information without as few
	# calls as possible to rpm.
	if ($rpmA =~ /([^-]+)-([^-]+)-([^-]+)$/) {
	    $verA = $1;
	    $relA = $2;
	    $epochA = $3;
	}
	else {
	    $nameA  = `rpm -q --qf '%{NAME}' $installedpkg`;
	    $verA  = `rpm -q --qf '%{VERSION}' $installedpkg`;
	    $relA  = `rpm -q --qf '%{RELEASE}' $installedpkg`;
	    $epochA  = `rpm -q --qf '%{EPOCH}' $installedpkg`;
	}
    }

    # Parse "none" as 0.
    if ($epochA =~ /(none)/) {
	$epochA = 0;
    }

    # Handle the case where only one of them is zero.
    if ($epochA == 0 xor $epochB == 0)
    {
	if ($epochA != 0)
	{
	    $retval = 1;
	}
	else
	{
	    $retval = 0;
	}
    }
    else
    {
	# ...otherwise they are either both 0 or both non-zero and
	# so the situation isn't trivial.

	# Check epoch first - highest epoch wins.
	my $rpmcmp = &cmp_vers_part($epochA, $epochB);
	#print "epoch rpmcmp is $rpmcmp\n";
	if ($rpmcmp > 0)
	{
	    $retval = 1;
	}
	elsif ($rpmcmp < 0)
	{
	    $retval = 0;
	}
	else
	{
	    # Epochs were the same.  Check Version now.
	    $rpmcmp = &cmp_vers_part($verA, $verB);
	    #print "epoch rpmcmp is $rpmcmp\n";
	    if ($rpmcmp > 0)
	    {
		$retval = 1;
	    }
	    elsif ($rpmcmp < 0)
	    {
		$retval = 0;
	    }
	    else
	    {
		# Versions were the same.  Check Release now.
		my $rpmcmp = &cmp_vers_part($relA, $relB);
		#print "epoch rpmcmp is $rpmcmp\n";
		if ($rpmcmp >= 0)
		{
		    $retval = 1;
		}
		elsif ($rpmcmp < 0)
		{
		    $retval = 0;
		}
	    }
	}
    }
    return $retval;
}

#################################################
#  Helper function for B_is_rpm_up_to_date()
#################################################

#This cmp_vers_part function taken from Kirk Bauer's Autorpm.
# This version comparison code was sent in by Robert Mitchell and, although
# not yet perfect, is better than the original one I had. He took the code
# from freshrpms and did some mods to it. Further mods by Simon Liddington
# <sjl96v@ecs.soton.ac.uk>.
#
# Splits string into minors on . and change from numeric to non-numeric
# characters. Minors are compared from the beginning of the string. If the
# minors are both numeric then they are numerically compared. If both minors
# are non-numeric and a single character they are alphabetically compared, if
# they are not a single character they are checked to be the same if the are not
# the result is unknown (currently we say the first is newer so that we have
# a choice to upgrade). If one minor is numeric and one non-numeric then the
# numeric one is newer as it has a longer version string.
# We also assume that (for example) .15 is equivalent to 0.15

sub cmp_vers_part($$) {
   my($va, $vb) = @_;
   my(@va_dots, @vb_dots);
   my($a, $b);
   my($i);

   if ($vb !~ /^pre/ and $va =~ s/^pre(\d+.*)$/$1/) {
      if ($va eq $vb) { return -1; }
   } elsif ($va !~ /^pre/ and $vb =~ s/^pre(\d+.*)$/$1/) {
      if ($va eq $vb) { return 1; }
   }

   @va_dots = split(/\./, $va);
   @vb_dots = split(/\./, $vb);

   $a = shift(@va_dots);
   $b = shift(@vb_dots);
   # We also assume that (for example) .15 is equivalent to 0.15
   if ($a eq '' && $va ne '') { $a = "0"; }
   if ($b eq '' && $vb ne '') { $b = "0"; }
   while ((defined($a) && $a ne '') || (defined($b) && $b ne '')) {
      # compare each minor from left to right
      if ((not defined($a)) || ($a eq '')) { return -1; } # the longer version is newer
      if ((not defined($b)) || ($b eq '')) { return  1; }
      if ($a =~ /^\d+$/ && $b =~ /^\d+$/) {
         # I have changed this so that when the two strings are numeric, but one or both
         # of them start with a 0, then do a string compare - Kirk Bauer - 5/28/99
         if ($a =~ /^0/ or $b =~ /^0/) {
            # We better string-compare so that netscape-4.6 is newer than netscape-4.08
            if ($a ne $b) {return ($a cmp $b);}
         }
         # numeric compare
         if ($a != $b) { return $a <=> $b; }
      } elsif ($a =~ /^\D+$/ && $b =~ /^\D+$/) {
         # string compare
         if (length($a) == 1 && length($b) == 1) {
            # only minors with one letter seem to be useful for versioning
            if ($a ne $b) { return $a cmp $b; }
         } elsif (($a cmp $b) != 0) {
            # otherwise we should at least check they are the same and if not say unknown
            # say newer for now so at least we get choice whether to upgrade or not
            return -1;
         }
      } elsif ( ($a =~ /^\D+$/ && $b =~ /^\d+$/) || ($a =~ /^\d+$/ && $b =~ /^\D+$/) ) {
         # if we get a number in one and a word in another the one with a number
         # has a longer version string
         if ($a =~ /^\d+$/) { return 1; }
         if ($b =~ /^\d+$/) { return -1; }
      } else {
         # minor needs splitting
         $a =~ /\d+/ || $a =~ /\D+/;
         # split the $a minor into numbers and non-numbers
         my @va_bits = ($`, $&, $');
         $b =~ /\d+/ || $b =~ /\D+/;
         # split the $b minor into numbers and non-numbers
         my @vb_bits = ($`, $&, $');
         for ( my $j=2; $j >= 0; $j--) {
            if ($va_bits[$j] ne '') { unshift(@va_dots,$va_bits[$j]); }
            if ($vb_bits[$j] ne '') { unshift(@vb_dots,$vb_bits[$j]); }
         }
      }
      $a = shift(@va_dots);
      $b = shift(@vb_dots);
   }
   return 0;
}

1;

