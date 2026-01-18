import { useStore } from "@nanostores/react";
import { $ws } from "../../common/nanostore";

const FibonacciWs = () => {
  const wsStore = useStore($ws);

  return (
    <div className="p-4 border rounded-lg shadow-sm">
        websocket
      <h1 className="text-xl font-bold">{wsStore.selected}</h1>
      <p className="text-gray-600">{wsStore.dataSource}</p>
    </div>
  );
};

export default FibonacciWs;
