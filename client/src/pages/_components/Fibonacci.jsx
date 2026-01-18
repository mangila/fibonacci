import FibonacciSse from "./FibonacciSse";
import FibonacciWs from "./FibonacciWs";
import { useStore } from "@nanostores/react";
import { $sse } from "../../common/nanostore";

const Fibonacci = () => {
  const sseStore = useStore($sse);

  return (
    <>
      {sseStore.selected ? (
        <>
          <FibonacciSse />
        </>
      ) : (
        <>
          <FibonacciWs />
        </>
      )}
    </>
  );
};

export default Fibonacci;
