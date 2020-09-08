cat log | while read line
do
  echo $line
  arr=($line)
  echo "docker save ${arr[2]} > ${arr[2]}.tag"
  docker save ${arr[2]} > ${arr[2]}.tag
  echo "-----------------------------"
done
