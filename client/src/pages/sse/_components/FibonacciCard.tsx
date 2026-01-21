import { useState, type RefObject } from "react";
import { Icon } from "@iconify/react";
import type { FibonacciData } from "../../_types/types";
import { queryById } from "../../_utils/api";
import { useEventListener } from "ahooks";
import type { EventEmitter } from "ahooks/lib/useEventEmitter";

interface Props {
  channel: string;
  streamKey: string;
  value: FibonacciData;
  event$: EventEmitter<FibonacciData>;
}

export const FibonacciCard = ({ channel, streamKey, value, event$ }: Props) => {
  const [hover, setHover] = useState(false);
  const [modalData, setModalData] = useState<FibonacciData>({
    id: 0,
    precision: 0,
    result: "",
  });

  event$.useSubscription((event) => {
    if (event.id == value.id) {
      setModalData(event);
    }
  });

  const openModal = () => {
    const element = document.getElementById(
      value.id.toString(),
    ) as HTMLDialogElement;
    if (element === null) {
      throw new Error(`modal not exist for: ${value.id}`);
    }
    element.showModal();
  };
  const closeModal = () => {
    const element = document.getElementById(
      value.id.toString(),
    ) as HTMLDialogElement;
    if (element === null) {
      throw new Error(`modal not exist for: ${value.id}`);
    }
    element.close();
  };

  return (
    <>
      <div
        className="border border-2 rounded-box p-2 link-hover"
        onClick={openModal}
        onMouseEnter={() => {
          if (!hover && streamKey !== null) {
            setHover(true);
            queryById(channel, streamKey, value.id);
          }
        }}
      >
        <span className="flex gap-1">
          <Icon
            icon="material-symbols:function"
            className="text-blue-500"
            style={{ fontSize: "36px" }}
          />
          <h3 className="font-bold">{value.id}</h3>
        </span>
      </div>
      <dialog id={value.id.toString()} className="modal" onClick={closeModal}>
        <div className="modal-box break-all prose">
          <h3 className="text-lg font-bold">
            Fibonacci Sequence: {modalData.id}
          </h3>
          <h4>Precison: {modalData.precision}</h4>
          <p>{modalData.result}</p>
          <p className="py-4">Press ESC key or click outside to close</p>
        </div>
      </dialog>
    </>
  );
};
