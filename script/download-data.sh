#!/bin/bash

script_dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
data_dir="${script_dir}/../data"
uk_url="http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/UK2010.xls"
ru_url="http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/Russia2011.zip"

mkdir -p "${data_dir}"

echo "Downloading ${uk_url}..."
if [ $(curl -s --head -w %{http_code} $uk_url -o /dev/null) -eq 200 ]; then
    curl -o "${data_dir}/UK2010.xls" $uk_url
else
    echo "Couldn't download UK data. Perhaps it has moved? Consult http://wiki.clojuredatascience.com"
fi

echo "Downloading ${ru_url}..."
if [ $(curl -s --head -w %{http_code} $ru_url -o /dev/null) -eq 200 ]; then
    curl -o "${data_dir}/Russia2011.zip" $ru_url
    tar xzf "${data_dir}/Russia2011.zip" --directory "${data_dir}"
else
    echo "Couldn't download Russia data. Perhaps it has moved? Consult http://wiki.clojuredatascience.com"
fi
