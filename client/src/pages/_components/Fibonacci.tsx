import FibonacciSse from "./FibonacciSse";
import FibonacciWs from "./FibonacciWs";
import { useStore } from "@nanostores/react";
import { $sseSelection, $wsSelection } from "../../common/nanostore";

const Fibonacci = () => {
  const sseSelected = useStore($sseSelection);
  const wsSelected = useStore($wsSelection);

  if (sseSelected) {
    return <FibonacciSse />;
  }

  if (wsSelected) {
    return <FibonacciWs />;
  }

  return (
    <div className="p-4 border rounded-lg shadow-sm">
      <h1 className="text-xl font-bold">No protocol selected</h1>
      <p className="text-gray-600">
        Please select a protocol to see Fibonacci data.
      </p>
    </div>
  );
};

export default Fibonacci;
