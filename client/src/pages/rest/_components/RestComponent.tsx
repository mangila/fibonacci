import { useDynamicList, useUpdateEffect } from "ahooks";
import { useActionState, useState } from "react";
import type { FibonacciDto, FibonacciProjectionDto } from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { QueryForm } from "./QueryForm";
import { CountCard } from "../../_components/CountCard";

import { queryList, queryById } from "../../../_service/service";

function handleSubmit(_, formData: FormData) {
  const offsetData = formData.get("offset");
  if (offsetData === null) {
    throw new Error("err");
  }
  const limitData = formData.get("limit");
  if (limitData === null) {
    throw new Error("err");
  }

  const offset = offsetData.toString();
  const limit = limitData.toString();

  return { offset, limit };
}

export const RestComponent = () => {
  const [state, formAction, isPending] = useActionState(handleSubmit, null);
  const { list, push, resetList } = useDynamicList<FibonacciProjectionDto>([]);
  const [modalData, setModalData] = useState<FibonacciDto>({
    id: 0,
    sequence: 0,
    precision: 0,
    result: "",
  });

  useUpdateEffect(() => {
    if (state) {
      const offset = state.offset;
      const limit = state.limit;
      queryList(limit, offset)
        .then((data) => {
          resetList(data);
        })
        .catch((err) => {
          alert(err);
        });
    }
  }, [state]);

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <QueryForm isPending={isPending} payload={formAction} />
      <div className="flex justify-center m-4">
        <CountCard count={list.length} />
      </div>
      <div className="grid grid-cols-3 md:grid-cols-12">
        {list.map((value) => {
          return (
            <div
              key={value.id}
              onClick={() => {
                queryById(value.id.toString())
                  .then((data) => {
                    setModalData(data);
                  })
                  .catch((err) => {
                    alert(err);
                  });
              }}
            >
              <FibonacciCard
                id={value.id}
                sequence={value.sequence}
                data={modalData}
              />
            </div>
          );
        })}
      </div>
    </ErrorBoundary>
  );
};
