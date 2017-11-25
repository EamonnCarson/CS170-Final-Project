for i in `seq 0 $1`; do
	java -jar FinalProjectSolver.jar input$2_$i
done
for i in `seq 0 $1`; do
	echo checking input$2_$i.out
	python3 output_validator.py input/input$2_$i.in output/input$2_$i.out
done
