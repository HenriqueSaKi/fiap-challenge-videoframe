#!/usr/bin/env bash


echo '                                                                                   '
echo '    _____ ____     _____             __ _                       _   _              '
echo '   / ____|___ \   / ____|           / _(_)                     | | (_)             '
echo '  | (___   __) | | |     ___  _ __ | |_ _  __ _ _   _ _ __ __ _| |_ _  ___  _ __   '
echo '   \___ \ |__ <  | |    / _ \| '\''_ \|  _| |/ _` | | | | '\''__/ _` | __| |/ _ \| '\''_ \  '
echo '   ____) |___) | | |___| (_) | | | | | | | (_| | |_| | | | (_| | |_| | (_) | | | | '
echo '  |_____/|____/   \_____\___/|_| |_|_| |_|\__, |\__,_|_|  \__,_|\__|_|\___/|_| |_| '
echo '                                           __/ |                                   '
echo '                                          |___/                                    '
echo '                                                                                   '

awslocal s3 mb s3://fiap-challenge-videoframe-in-bucket
awslocal s3 mb s3://fiap-challenge-videoframe-out-bucket