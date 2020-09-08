for rar_file in `ls $1`
do
  file_name=$rar_file
  file_type=${file_name##*.}
  if [ -d $file_name ]
    then echo 'this is dir'
  else
    echo "this is file $file_name --> $file_type"
    if [ "$file_type" == "tar" ]
      then docker load < $file_name
    else
      echo 'skip'
    fi
  fi
done
