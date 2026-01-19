import FibonacciSse from "./FibonacciSse";
import FibonacciWs from "./FibonacciWs";
import { useStore } from "@nanostores/react";
import { $sseSelection, $wsSelection } from "../_utils/nanostore";
import { Suspense } from "react";
import { ErrorBoundary } from "react-error-boundary";

const Fibonacci = () => {
  const sseSelected = useStore($sseSelection);
  const wsSelected = useStore($wsSelection);

  if (sseSelected) {
    return (
      <>
        <ErrorBoundary fallback={"the human is err..."}>
          <Suspense fallback={"loading..."}>
            <FibonacciSse />
          </Suspense>
        </ErrorBoundary>
      </>
    );
  }

  if (wsSelected) {
    return (
      <>
        <ErrorBoundary fallback={"the err is human..."}>
          <Suspense fallback={"loading..."}>
            <FibonacciWs />
          </Suspense>
        </ErrorBoundary>
      </>
    );
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
