import { useStore } from "@nanostores/react";
import { $sse } from "../../common/nanostore";

const FibonacciSse = () => {
  const sseStore = useStore($sse);

  return (
    <div className="p-4 border rounded-lg shadow-sm">
        sse
      <h1 className="text-xl font-bold">{sseStore.selected}</h1>
      <p className="text-gray-600">{sseStore.dataSource}</p>
    </div>
  );
};

export default FibonacciSse;
