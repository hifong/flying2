mkdir {$compile_folder}
cd {$compile_folder}
svn copy {$version_code_base} {$tag_code_base} -m "{$version} released"
svn checkout {$tag_code_base} {$compile_folder} --username king
mvn clean package -f {$compile_folder}\pom.xml