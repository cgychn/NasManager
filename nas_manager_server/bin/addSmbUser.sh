#!/usr/bin/expect
set username [lindex $argv 0]
set password [lindex $argv 1]
spawn smbpasswd -a $username
expect "New SMB password:"
send "$password\n"
expect "Retype new SMB password:"
send "$password\n"
expect eof