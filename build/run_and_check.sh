start=$SECONDS
java -jar FinalProjectSolver.jar $1
duration=$((  SECONDS - start ))
hours=$(( duration / 3600 ))
minutes=$(( (duration % 3600) / 60 ))
seconds=$(( (duration % 3600) % 60 ))
echo checking $1.out
python3 output_validator.py input/$1.in output/$1.out
echo Solver took $hours hours, $minutes minutes, and $second seconds to complete.
