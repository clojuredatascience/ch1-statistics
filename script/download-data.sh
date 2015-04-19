#!/bin/bash

script_dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
resources_dir=${script_dir}/../resources

curl -o ${resources_dir}/UK2010.xls http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/UK2010.xls

curl -o ${resources_dir}/Russia2011.zip http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/Russia2011.zip

tar xzf ${resources_dir}/Russia2011.zip --directory "${resources_dir}"
