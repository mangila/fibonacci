import { Icon } from "@iconify/react";
import type { FibonacciData } from "../_types/types";

interface Props {
  id: number;
  data: FibonacciData;
}

export const FibonacciCard = ({ id, data }: Props) => {
  const openModal = () => {
    const element = document.getElementById(id.toString()) as HTMLDialogElement;
    if (element === null) {
      throw new Error(`modal not exist for: ${id}`);
    }
    element.showModal();
  };
  const closeModal = () => {
    const element = document.getElementById(id.toString()) as HTMLDialogElement;
    if (element === null) {
      throw new Error(`modal not exist for: ${id}`);
    }
    element.close();
  };

  return (
    <>
      <div
        className="border border-2 rounded-box p-2 link-hover"
        onClick={openModal}
      >
        <span className="flex gap-1">
          <Icon
            icon="material-symbols:function"
            className="text-blue-500"
            style={{ fontSize: "36px" }}
          />
          <h3 className="font-bold">{id}</h3>
        </span>
      </div>
      <dialog id={id.toString()} className="modal" onClick={closeModal}>
        <div className="modal-box break-all prose">
          <h3 className="text-lg font-bold">Fibonacci Sequence: {data.id}</h3>
          <h4>Precison: {data.precision}</h4>
          <p>{data.result}</p>
          <p className="py-4">Press ESC key or click outside to close</p>
        </div>
      </dialog>
    </>
  );
};
